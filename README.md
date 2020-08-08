# Chat Server - Project Requirements
### Chat Room Requirements
You must write server and client programs to let users exchange messages. Each user will run the ChatClient program and connect to a shared ChatServer.  The server will be responsible for forwarding incoming messages to all clients that are connected.

Typing a message and hitting send should transmit the desired message to the server, which will in turn forward the message to any other client that is connected. The server should also print something to its screen such as: "Joe: Hi I just joined" indicating that a message was sent by the user Joe.

Users should be able to change their name by sending the message "/name newName". This will cause all subsequent messages to be sent from the new name.
