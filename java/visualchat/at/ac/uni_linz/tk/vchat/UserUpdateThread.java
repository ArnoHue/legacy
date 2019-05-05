package at.ac.uni_linz.tk.vchat;

import java.io.*;
import java.net.*;
import java.awt.*;


/**
 * A Thread running in the ChatServer's environment handling a UserUpdateEvent.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class UserUpdateThread extends Thread {

  private ChatServer server;
  private Connection connection;
  private UserUpdateEvent updateEvent;
  private User oldUser;
  private int overallAvatarSize;


/**
 * Constructs the Connection.
 *
 * @param updateEventParam      the UserUpdateEvent produced by a User
 * @param serverParam           the ChatServer, which initiated the
 *                              UserUpdateThread
 */

  public UserUpdateThread(UserUpdateEvent updateEventParam, ChatServer serverParam, Connection connectionParam) {
    server = serverParam;
    connection = connectionParam;
    updateEvent = updateEventParam;
    oldUser = server.loadUser(updateEvent.user.getName());
  }


/**
 * Runs the UserUpdateThread, which load the User's new avatars over the network.
 */

  public void run() {
  if (oldUser == null) {
    return;
    }
    String newPwd = updateEvent.user.getPassword();
    if (newPwd != null && newPwd.length() > 0) {
      if (!newPwd.equals(oldUser.getPassword())) {
          server.log("Wrong password");
          return;
        }
      }
    else {
      updateEvent.user.setPassword(oldUser.getPassword());
    }

    server.log("Updating user avatars...");

    overallAvatarSize = 0;

    updateAvatar(-1, updateEvent.user.getBackAvatarURL());
    for (int i = 0; i < updateEvent.user.getNrOfMoods(); i++) {
      updateAvatar(i, updateEvent.user.getAvatarURL(i));
    }
    updateEvent.done = true;
    if (updateEvent.statusString == null || updateEvent.statusString.equals("")) {
      updateEvent.statusString = "User Update successful";
    }
    server.handleUserEvent(updateEvent, connection);
  }


/**
 * Controls the retrieving process for an user avatar. The loading of the image itself is
 * done by getImage().
 *
 * @param portraitIndex      the Avatar's index
 * @param imageURL           the Image's URL or filename
 */

  private void updateAvatar(int portraitIndex, String imageName) {
    if (imageName != null && imageName.length() > 0 && !imageName.equals(portraitIndex == -1 ? oldUser.getBackAvatarURL() : oldUser.getAvatarURL(portraitIndex))) {
      boolean res = false;
      if (imageName.startsWith("http://")) {
        try {
          res = updateAvatarImpl(portraitIndex, new URL(imageName));
        }
        catch (MalformedURLException e) {
          reportAvatarUpdateFailure(portraitIndex, "Invalid URL: " + imageName);
        }
      }
      else {
        if (imageName.indexOf("/") != - 1 || imageName.indexOf("\\") != - 1 || imageName.indexOf(":") != - 1) {
          reportAvatarUpdateFailure(portraitIndex, "Invalid name: " + imageName);
        }
        else {
          res = updateAvatarImpl(portraitIndex, new File(server.getImageFolder() + imageName));
        }
      }
      if (!res) {
        resetAvatar(portraitIndex);
      }
    }
    else if (imageName == null || imageName.length() == 0) {
      if (portraitIndex == -1) {
        updateEvent.user.setBackAvatarURL("");
        updateEvent.user.setBackAvatar(null);
      }
      else {
        updateEvent.user.setAvatarURL(portraitIndex, "");
        updateEvent.user.setAvatar(portraitIndex, null);
      }
    }
    else {
      resetAvatar(portraitIndex);
    }
  // server.log("Overall avatar size so far: " + overallAvatarSize + " bytes");
  }


/**
 * Resets a User's Image to the most recent one that was stored.
 *
 * @param portraitIndex      the Image's index (equals the mood it is related to)
 */

  private void resetAvatar(int portraitIndex) {
    if (portraitIndex == -1) {
      updateEvent.user.setBackAvatarURL(oldUser.getBackAvatarURL());
      updateEvent.user.setBackAvatar(oldUser.getBackAvatar());
    }
    else {
      updateEvent.user.setAvatarURL(portraitIndex, oldUser.getNrOfMoods() > portraitIndex ? oldUser.getAvatarURL(portraitIndex) : "");
      updateEvent.user.setAvatar(portraitIndex, oldUser.getNrOfMoods() > portraitIndex ? oldUser.getAvatar(portraitIndex) : null);
    }
  }


/**
 * Loads an Image from a WebServer or the local filesystem.
 *
 * @param portraitIndex      the Avatar's index
 * @param imageName          the Avatar's URL or filename
 * @param isURL              true if imageName is an URL, false if not
 */

  private void setAvatar(int portraitIndex, byte[] buffer) {
    if (portraitIndex == -1) {
      updateEvent.user.setBackAvatarBuffer(buffer);
    }
    else {
      updateEvent.user.setAvatarBuffer(portraitIndex, buffer);
    }
  }


  private boolean checkAvatarSize(int portraitIndex, String name, long size) {
    if (size > ChatRepository.MAX_PORTRAIT_SIZE) {
      reportAvatarUpdateFailure(portraitIndex, "Avatar " + name + " exceeds maximum size");
      return false;
    }
    else if (overallAvatarSize + size > ChatRepository.MAX_USERFILE_SIZE) {
      reportAvatarUpdateFailure(portraitIndex, "With avatar " + name + " the overall user-file size exceeds maximum file length");
      return false;
    }
    else {
      return true;
    }
  }

  private boolean updateAvatarImpl(int portraitIndex, URL url) {
    server.log("Updating avatar to " + url);
    URLConnection conn = null;
    try {
      conn = url.openConnection();
      long size = conn.getContentLength();
      String content = conn.getHeaderField("content-type");
      if (content != null) {
        content = content.toLowerCase();
      }
      if (content == null || content.indexOf("html") != -1 || conn.getHeaderField(0).indexOf("404") != -1) {
        reportAvatarUpdateFailure(portraitIndex, "Avatar " + url + " doesn't exist on the web");
        return false;
      }
      if (content.indexOf("gif") == -1 && content.indexOf("jpg") == -1 && content.indexOf("jpeg") == -1) {
        reportAvatarUpdateFailure(portraitIndex, "Avatar " + url +" has wrong format, only GIFs and JPEGs supported");
        return false;
      }
      if (checkAvatarSize(portraitIndex, url.toString(), conn.getContentLength())) {
        byte[] buffer = ChatUtil.getBinaryContent(url);
        if (checkAvatarSize(portraitIndex, url.toString(), buffer.length)) {
          setAvatar(portraitIndex, ChatUtil.getBinaryContent(url));
          overallAvatarSize += size;
          return true;
        }
      }
    }
    catch (IOException excpt) {
      reportAvatarUpdateFailure(portraitIndex, "Avatar " + url + " doesn't exist on the web");
    }
    catch (Exception excpt) {
      reportAvatarUpdateFailure(portraitIndex, excpt.getMessage());
    }
    finally {
      try {
        if (conn != null && conn.getInputStream() != null) {
          conn.getInputStream().close();
        }
      }
      catch (Exception ex) {
      }
    }
    return false;
  }

  private boolean updateAvatarImpl(int portraitIndex, File file) {
    server.log("Updating avatar to " + file);
    if (!file.exists()) {
      reportAvatarUpdateFailure(portraitIndex, "Avatar " + file + " doesn't exist on the server");
      return false;
    }
    else if (!checkAvatarSize(portraitIndex, file.toString(), file.length())) {
      return false;
    }
    else {
      try {
        setAvatar(portraitIndex, ChatUtil.getBinaryContent(file.getPath()));
        overallAvatarSize += file.length();
        return true;
      }
      catch (IOException ex) {
        reportAvatarUpdateFailure(portraitIndex, ex.getMessage());
        return false;
      }
    }
  }

  private void reportAvatarUpdateFailure(int portraitIndex, String str) {
    server.log(str);
    updateEvent.statusString = str;
    resetAvatar(portraitIndex);
  }

}
