# SDIS - 2nd Project Specification

<p align="right">24 de Abril de 2015</p>


##### T1G05

- Henrique Ferrolho  
- João Pereira  
- Pedro Castro  
- Vânia Leite  


## Purpose of the Application

The group proposes to develop a cross-platform collaborative drawing application.

The application will have two main screens: a *main menu* screen and a *room* screen.  
In the **main menu**, users will be able to *join* existing rooms, or to *create* new rooms.  
Users are presented with the **room screen** after having joined or created a room. Each room has a *canvas* where its users will be able to draw, sketch, or paint collaboratively with each other. The canvas of each room is unique, and therefore what is drawn in the canvas of a room is not drawn on any other canvas of other rooms.


## Main Features

When users start the app they are presented with a *menu* containing three buttons. Each button *deploys* one of the following *actions*:

#### **Join** a *public* room

After pressing this button, users are presented with a **list** of *public* online rooms. Users are free to pick one of the rooms and start drawing on the canvas along with the other users in the room.

#### **Join** a *private* room

Private rooms require users to specify a room ID and a password to be granted access.

This type of rooms can be used when users want to restrain access to a room.

#### **Create** a *new* room

Users can create new rooms with this button. They will be asked to input the *canvas size*, and whether the room should be *public* or *private*. In the latter case, the *room ID* and *password* also need to be specified.

## Target Platforms

- Java standalone application for PC/Mac
- Application for mobile devices:
  - Android


## Additional Services and Improvements

The group will implement the basis architecture to develop a usable application. After this, the group will improve the application in order to be able to achieve a better grade.

Following are the improvements expected to be made.

### Architecture

The application should *not* usa a Server-Client architecture. There should be a server to keep the current list of available rooms online *only*.

The processing of events should be made by the device which created the room, without requiring any interaction with the server.  
Moreover, a better approach would be to move the responsability of processing/managing the room to the most powerful device. This could be done when a user enters the room: if the user's device is more powerful than the host device, the application should "put the new user in charge" of hosting that room.

### Scalability

The application should scale with ease: a similar performance should be achieved, whether 1 or 1000 users are in the room. The group considers this will come as a good consequence of the architecture improvement descrived in the previous topic.

### Consistency

The application should be consistent, i.e. it should correctly manage concurrent events and canvas drawings. For example: if user A starts drawing a line, and meanwhile user B draws a line on top of the region where A has already drawn, the application should decide correctly which of the lines sits on top of the other.

### Authentication

Access to private rooms should require authentication: users need to input the *room ID* and *password*.

### Fault tolerance

The application should tolerate faults with ease, such as temporary internet disconnections.

## Proposed grade ceiling

Given the described features, and application architecture, the group considers 20 to be a valid ceiling for the final grade.
