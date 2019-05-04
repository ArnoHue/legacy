/***********************************************************************\
*                                                                      *
*     Minishell: Command execution                                     *
*     (C) 2000 by Arno Huetter                                         *
*                                                                      *
\***********************************************************************/

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <glob.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "msh.h"

#define FALSE 0
#define TRUE 1

#define MAX_NR_OF_VARS 100

char *env[MAX_NR_OF_VARS];

int nr_of_vars = 0;

int exec_command(Simple_Cmd simple_cmd, int bg_mode) {
  int i, done, ret;
  Word *word;
  Assign *assign;
  glob_t args;
  glob_t cmd;
  char buffer[1024];

  ret = 0;
  done = FALSE;

  for (assign = simple_cmd.env; assign; assign = assign->next) {
    strcpy(buffer, assign->name);
    strcat(buffer, "=");
    putenv(strcat(buffer, assign->value->chars));
  }

  args.gl_offs = 0;
  cmd.gl_offs = 0;
  word = simple_cmd.word;
  glob(word->chars, GLOB_NOCHECK, NULL, &cmd);

  for (i = 0; word != NULL; i++) {
    glob(word->chars, i == 0 ? GLOB_NOCHECK : GLOB_NOCHECK | GLOB_APPEND, NULL, &args);
    word = word->next;
  }

  if (strcmp(simple_cmd.word->chars, "exit") == 0) {
    done = TRUE;
    exit(args.gl_pathc < 2 ? 0 : atoi(args.gl_pathv[1]));
  }
  else if (strchr(cmd.gl_pathv[0], 'cd') != NULL) {
    done = TRUE;
    chdir(simple_cmd.word->next != NULL ? simple_cmd.word->next->chars : ".");
  }
  else if (strchr(cmd.gl_pathv[0], '=') != NULL) {
    done = TRUE;
    if (nr_of_vars < MAX_NR_OF_VARS) {
      env[nr_of_vars++] = cmd.gl_pathv[0];
    }
    else {
      perror("variable overflow");
      return -1;
    }
  }
  if (strcmp(simple_cmd.word->chars, "export") == 0) {
    done = TRUE;
    for (i = 0; i < nr_of_vars; i++) {
      putenv(env[i]);
    }
    nr_of_vars = 0;
  }

  if (!done) {
    return execvp(cmd.gl_pathv[0], args.gl_pathv);
  }

  return ret;
}


int exec_if(If_Cmd *if_cmd) {
  return (if_cmd->test == NULL || exec_list(if_cmd->test) == 0) ? exec_list(if_cmd->true) : exec_if(if_cmd->false);
}


int exec_while(While_Cmd while_cmd) {
  int ret = 0;
  while(exec_list(while_cmd.test) == 0) {
    ret = exec_list(while_cmd.body);
  }
  return ret;
}


void exec_redir(Redirection *redir) {
  int f;
  Redirection *cur_redir = redir;

  for (cur_redir=redir; cur_redir; cur_redir=redir->next) {
    if (cur_redir->oflags >= 0) {
      if ((f = open(cur_redir->fname->chars, cur_redir->oflags, 0666))) {
        close(cur_redir->fd);
        dup(f);
        close(f);
     }
    }
    else {
      close(cur_redir->fd);
      dup(cur_redir->ofd);
    }
  }
}


int exec_pipe_internal(Pipeline *pipeline, int bg_mode) {
  switch(pipeline->type) {
    case tEOF:
      return exec_command(pipeline->cmd.simple, bg_mode);
      break;
    case tIF:
      return exec_if(pipeline->cmd._if);
      break;
    case tWHILE:
      return exec_while(pipeline->cmd._while);
      break;
    default:
      perror("exec_pipe_internal");
      return -1;
      break;
  }
  return 0;
}


int exec_pipe(Pipeline *pipeline, int bg_mode) {
  int ret = 0;
  int pid1,pid2;
  int builtin = 0;
  int fds[2];

  if (pipeline->prev != NULL) {
    pid1 = fork();
    switch(pid1) {
      case -1:
        perror("exec_pipe");
        exit(-1);
        break;
      case 0:
        pipe(fds);
        pid2 = fork();
        switch(pid2) {
          case -1:
            perror("exec_pipe");
            exit(-1);
            break;
          case 0:
            close(1);
            dup(fds[1]);
            close(fds[1]);
            close(fds[0]);
            if (pipeline->redir != NULL) {
              exec_redir(pipeline->redir);
            }
            exit(exec_pipe(pipeline, bg_mode));
            break;
          default:
            close(0);
            dup(fds[0]);
            close(fds[0]);
            close(fds[1]);
            if (pipeline->redir != NULL) {
              exec_redir(pipeline->redir);
            }
            exit(exec_pipe(pipeline->prev, bg_mode));
            break;
        }
        break;
      default:
        if (!bg_mode) {
          waitpid(pid1, &ret, 0);
        }
        break;
    }
  }
  else {
    builtin = (pipeline->type == tEOF && (!strcmp(pipeline->cmd.simple.word->chars, "exit") ||
                                          !strcmp(pipeline->cmd.simple.word->chars, "cd") ||
                                          !strcmp(pipeline->cmd.simple.word->chars, "export") ||
                                           strchr(pipeline->cmd.simple.word->chars, '=') != NULL));

    if (pipeline->redir != NULL || !builtin) {
      pid1 = fork();
      switch(pid1) {
        case -1:
          perror("exec_pipe");
          exit(-1);
          break;
        case 0:
          if (pipeline->redir != NULL) {
            exec_redir(pipeline->redir);
          }
          exit(exec_pipe_internal(pipeline, bg_mode));
          break;
        default:
          if (!bg_mode) {
            waitpid(pid1, &ret, 0);
          }
          break;
      }
    }
    else if (builtin) {
      ret = exec_pipe_internal(pipeline, bg_mode);
    }
  }
  return pipeline->bang || ret;
}


int exec_andor(Andor *andor, int bg_mode) {
  int ret = 0;
  if (andor->pipe != NULL) {
    ret = exec_pipe(andor->pipe, bg_mode);
  }
  if (andor->next) {
    if (andor->term == tOR) {
      ret |= exec_andor(andor->next, bg_mode);
    }
    else if (andor->term == tAND) {
      ret &= exec_andor(andor->next, bg_mode);
    }
    else {
      ret = 1;
    }
  }
  return ret;
}


int exec_list(List *list) {
  int ret = 0;
  if (list->andor != NULL) {
    ret = exec_andor(list->andor, list->term == '&');
  }
  if (list->next != NULL) {
    ret = exec_list(list->next);
  }
  return ret;
}
