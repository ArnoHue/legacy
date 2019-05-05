package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.util.*;


/**
 * Simulates Users for the demo-mode. Those Users will walk around and send
 * random messages.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class Simulator extends Thread {

  private final int STEPS = 20;
  private final int DELAY = 1000;
  private final int STEP_DELAY = 100;
  private final double PROBABILITY_NONE = 0.1;
  private final double PROBABILITY_TALKING = 0.4;
  private final double PROBABILITY_MOOD = 0.05;
  private final double PROBABILITY_MOVING_TO_CURRENT = 0.1;
  private final double PROBABILITY_MOVING_TO_OTHER = 0.25;
  private final double PROBABILITY_MOVING = 0.06;
  private final double PROBABILITY_ROTATING = 0.02;
  private final double PROBABILITY_JUMPING = 0.02;

  private Vector robotVector;
  private ChatApplet chatApplet;


/**
 * Constructs the Simulator.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                users
 */

  public Simulator (ChatApplet chatAdministratorParam) {
    super();
    chatApplet = chatAdministratorParam;
    robotVector = new Vector();
  }


/**
 * Adds a User to the Simulator.
 *
 * @param userParam      the User to be added
 */

  public void addUser(User userParam) {
    if (userParam.getRace() == User.ROBOT_RACE)
      robotVector.addElement(new Integer(userParam.getId()));
  }


/**
 * Removes a User from the Simulator.
 *
 * @param userParam      the User to be added
 */

  public void removeUser(int userIdParam) {
    robotVector.removeElement(new Integer(userIdParam));
  }


/**
 * Runs the Simulator in an own Thread.
 */

  public void run() {
    Random randomizer = new Random();
    User robot;
    int deltaAngle, deltaX, deltaY, robotIndex;
    double eventId;

    while(true) {
      try {
        sleep(DELAY);
      }
      catch (InterruptedException exception)
      { }
      if (robotVector.size() > 0) {
        eventId = randomizer.nextDouble();
        if (eventId <= PROBABILITY_NONE) {
        }
        else {
          robot = chatApplet.getUser(((Integer)robotVector.elementAt(robotIndex = (int)(robotVector.size() * randomizer.nextDouble()))).intValue());
          if (robot != null) {
            if (eventId <= PROBABILITY_NONE + PROBABILITY_TALKING) {
              chatApplet.setUserMessage(robot.getId(), ChatRepository.ROBOT_MESSAGE[robotIndex][(int)(ChatRepository.ROBOT_MESSAGE[robotIndex].length * randomizer.nextDouble())], false);
            }
            else if (eventId <= PROBABILITY_NONE + PROBABILITY_TALKING + PROBABILITY_MOOD) {
              chatApplet.setUserMood(robot.getId(), (int)(ChatRepository.PREDEFINED_NR_OF_MOODS * randomizer.nextFloat()), false);
            }
            else if (eventId <= PROBABILITY_NONE + PROBABILITY_TALKING + PROBABILITY_MOOD + PROBABILITY_ROTATING) {
              deltaAngle = (int)(randomizer.nextFloat() < 0.5 ? chatApplet.inVisualRange(chatApplet.getCurrentUserId(), robot.getId()) ? 180 : 0 : 360 * randomizer.nextFloat() / STEPS);
              for (int i = 0; i < STEPS; i++) {
                chatApplet.setUserHeading(robot.getId(), ChatUtil.addAngle(robot.getHeading(), deltaAngle), false);
                try {
                  sleep(STEP_DELAY);
                }
                catch (InterruptedException exception)
                { }
              }
            }
            else if (eventId <= PROBABILITY_NONE + PROBABILITY_TALKING + PROBABILITY_MOOD + PROBABILITY_ROTATING + PROBABILITY_JUMPING) {
              chatApplet.moveUserToRoom(robot.getId(), randomizer.nextFloat() < 0.5 ? chatApplet.getCurrentRoomId(): (int)(randomizer.nextFloat() * chatApplet.getNrOfRooms()), true);
            }
            else {
              User user;
              Point targetPosition;
              int targetHeading;

              if (eventId <= 1 - PROBABILITY_MOVING_TO_CURRENT - PROBABILITY_MOVING_TO_OTHER) {
                targetPosition = new Point((int)(ChatRepository.ROOM_DIMENSION.width * randomizer.nextFloat()), (int)(ChatRepository.ROOM_DIMENSION.height * randomizer.nextFloat()));
                targetHeading = robot.getHeading();
              }
              else {
                if (eventId <= 1 - PROBABILITY_MOVING_TO_OTHER)
                  user = chatApplet.getRandomUser();
                else
                  user = chatApplet.getCurrentUser();
                targetPosition = user.getPosition();
                targetHeading = ChatUtil.addAngle(user.getHeading(), chatApplet.inVisualRange(user.getId(), robot.getId()) ? 180 : 0);
              }
              deltaX = (targetPosition.x - robot.getPosition().x) / STEPS;
              deltaY = (targetPosition.y - robot.getPosition().y) / STEPS;
              deltaAngle = (int)(ChatUtil.subAngle(targetHeading, robot.getHeading()) / STEPS);
              for (int i = 0; i < STEPS; i++) {
                chatApplet.setUserPosition(robot.getId(), new Point(robot.getPosition().x + deltaX, robot.getPosition().y + deltaY), false);
                chatApplet.setUserHeading(robot.getId(), ChatUtil.addAngle(robot.getHeading(), deltaAngle), false);
                try {
                  sleep(STEP_DELAY);
                }
                catch (InterruptedException exception)
                { }
              }
            }
          }
        }
      }
    }
  }

}

