# Chat-room-confidentiality-and-integrity
No security measures are currently in place for this chat service.

At this stage, you are going to upgrade this service in terms of security.

Typically, the first step in a secure communication protocol for both parties is to authenticate each other and negotiate a common secret key.

In this exercise, the goal is not to implement authentication, and now just assume that the two parties have authenticated each other and established a common secret key.

To simulate a shared secret key, you must call the getValue.InsecureSharedValue () function.

See the java.InsecureSharedValue file. This value is read from the config file and we assume that this is a hidden value that only the client and the server know about.

So the confidentiality and integrity of the message in this exercise is our goal. To do this, the data exchanged in this system must be encrypted in the transmitter and decrypted in the receiver. So the purpose of a connection is encrypted.

Your code must eventually win the attack with the following assumptions:

*%* The enemy knows your encryption algorithm. That is, when it sniffs and receives encrypted text, the decryption algorithm knows it.

*%* The enemy can see all the network traffic.

*%*The enemy can generate fake network traffic.

*%*The enemy does not have the value getValue.InsecureSharedValue()


If the enemy fails to do the following, you win :)

- Create a fake chat message and send it on behalf of a user.

- Understand the content of the chat message.

- Create a fake chat message and send it on behalf of a user.

**********************************************************************************

* Run : 
1. Open Cmd and go to the path where you saved the file above.

2.Compile the code using the "make" or "java * .javac" command. (If you have a problem, the keywords JDK and Search for a variable environment and fix the problem).

3.Open four cmd pages to get started. Put the path of all where you compiled the code.

4.In the first cmd, type the command "ChatServer java". With this command, the ChatServer program starts running. Do not wait for the output. In fact, this program is waiting for a connection from the client.

5.Go to the second cmd and type the command "user1 ChatClient java". By executing this command, a client named user1 We built. Do not wait for the output here either. If you look at the cmd server, a message is displayed that connection established.

6.In the third and fourth cmd, create two more clients with different names. By doing this, there are three clients in our room chat has it.

7.You are now ready to chat. Write a text on the cmd page of one of the clients and send it with enter do. You will see that this message has appeared on the page of the other two clients.

*********************************************************************************

The initial unsecured source code is inserted in ChatRoom.zip.


