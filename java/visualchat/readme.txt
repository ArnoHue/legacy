Visual Chat 2.72 Developer Edition

- Customize the Visual Chat code regarding your own requirements
- Compile from the visualchat_source-directory: "javac at\ac\uni_linz\tk\vchat\*.java at\ac\uni_linz\tk\vchat\engine\*.java"
- Start the server (again from the visualchat_source-directory) by invoking: "java at.ac.uni_linz.tk.vchat.ChatServer"
- Use customchatdev.html for developing / testing (open it in appletviewer or browser - important: open via webserver and http, not via filesystem)
- Create compressed .jar and .cab-files containing client-specific .class-files and the images-folder (use zip and cabarc compressing tools), e.g.
  zip -9 chat.jar at\ac\uni_linz\tk\vchat\*.class at\ac\uni_linz\tk\vchat\engine\*.class symantec\itools\awt\*.class symantec\itools\lang\*.class images\*.gif images\*.jpg
  cabarc -r -p N chat.cab *.class images\*.gif images\*.jpg
- Adapt the customchat.html file
- Upload all the files to your webserver
- I kindly ask you to leave copyright and credit information in the InfoPanel.class as it is - but you are invited to add your own text.