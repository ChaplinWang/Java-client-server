
1.How bank and shop talk:
Report
shop establish connection with bank (client is shop bank is server). client send a client sentence containing transaction information in following format: “firstname&familyname&cardnumber&spending” e.g.“John&Lennon&12345678&150” bank read sentence and takes variables out and look up database for mapping.Once finished mapping, bank send a sentence to shop:”nouser” or “approved” or "insufficient" and change its database,and connection between bank and shop closed. once shop received information, it prints relevant pages to the user.

1.1 establishing connect issue:
when shop trying to connect with the bank, it will reconnect bank if connection failed. there is a while loop handling this, it keeps trying to connect bank until connection established.
1.2 data format error checking in the bank server:
user-name family name card number are case sensitive and cannot contain special characters: there is a line checking special characters: Pattern namePattern = Pattern.compile("/[^a-z0-9 ]/gi"); bank account must be longer or equal to 8 number.
2.Shop and Client communication:
Shop implemented HTTP1.o protocol which close connection directly after sending the required file or finished handing client request. Shop server implemented 2 method handling user information: GET and POST methods.
2.1 GET
it takes the first client sentence to check required documentation.The printPage(String filename, DataOutputStream outToClient) function will search file name in current directory and send required file to client once find it or send "HTTP/1.0 404 Not found\r\n" response and 404.html to user if the required file cannot be found.
2.2 POST
POST will first find content length of the message by the client message "Content- Length: "and read char by char to get message from client.all post message will be stored as key value pair in a hash map. The shop will sum up total cost of order by looking up price.txt and suburb.txt.
3 Usage
Chengbin Wang z3313137 Cbwa548

3.1 to start bank server:
% java bank BANK_PORT
3.2 to start shop server:
% java shop SHOP_PORT BANK_HOST_IP BANK_PORT
3.3 The user will open a Firefox web browser and type the following url:
http://SHOP_HOST_IP:SHOP_PORT/index.html
3.4 user can order etc.. 4.important assumption:
user will always fill in all information: if not the server will went down.
assumption comes from spec:
“You may assume that a user will always fill in all information, which is in the correct format i.e. the name fields will always contain alphabets; item number, quantity, postcode and credit card number are all numbers. You do not have to worry about handling these errors.”

