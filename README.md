# perisystemjava

### What is Peri?
From Persian mythology, the Peri are fairies with wings.  Sort of like genies.  Everyone wants to make money in the stock market, but if you are like me, you don't want to spend hours analyzing stocks.  You want to leverage data and make decisions based on rules--and hope it pays off in your favor.  Algorithmic trading is hard.  But, that doesn't mean the average person shouldn't be able to try their hand at it.  And that is the goal of Peri.  To build a framework that anyone with some basic programming experience in Java can quickly download and start writing meaningful code in, without having to deal with the annoying parts.

### Introduction
The Peri System is a framework for developing stock trading strategies.  Documentation on how to do so will be published shortly and available in the README section of this repository by November 2017.
This project was mostly worked on in late 2016 and early 2017, but I have taken a step away from it to focus on other things.  I am going to return to it shortly to improve it and add onto it some of the desired behavior present in the "Future" section of this README.

### Highlights of the Peri System:
Built in Java
Built on top of the Bloomberg API
Built on top of TA-lib

### Key Goals:
Technical market data -- price, volume, historical prices, etc.
Fundamental market data -- p/e ratio, industry info, earnings expectations, etc.
The ability to combine these types of data.
The ability to analyze equities from around the world.
The ability to write simple strategies, starting from a list of stocks (usually an index), and filtering these names to a buy and sell list.
Framework approach -- anyone should be able to download the source code, add a strategy of their own within hours, and start trading.
Trading should be automatic.
Profit and loss reporting via daily notifications.

### Results
While I have most definitely not achieved everything in the "Key Goals" section, some progress has been made.  We have a connection to the Bloomberg API which is user-friendly and really allows for some basic strategies to be implemented right off the bat
We have the ability to make elegant buy and sell lists in Microsoft Excel format.
We have a framework approach that allows others to add onto the system with their own strategies.
Some of the more ambitious goals have not yet been achieved.  Trading automation being the main one.

### Future
Within the coming months I plan on implementing trading automation to allow for trade execution through the interactive brokers API.  This is an ambitious task that requires careful coding.  Users (including myself) will want to ensure that trades are executed correctly without error.

### Questions or Comments?
Feel free to email me at adhia@usc.edu
