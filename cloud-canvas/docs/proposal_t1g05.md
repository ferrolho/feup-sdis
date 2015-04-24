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

### Authentication

### Fault tolerance

### Consistency

### Scalability


## Proposed grade ceiling

Given the described features, and application architecture, the group considers 20 to be a valid ceiling for the final grade.
