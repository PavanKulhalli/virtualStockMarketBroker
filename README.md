# VirtualStockMarketBroker

Virtual Stock Market is a fast paced and interactive stock market simulation. It is a simulation of stock
market for multiple users - having entities like client, broker, stock market, bank and various companies.
Users can log in the system and buy or sell stock/s. Users will be able to view their existing balance,
number and name of shares they have in their account. They are provided with facility to buy and sell
stocks, as expected to the very least in such a system. The stock price variation is kept simple, where the
price changes to the last transaction rate.

The aim was to have a simulation available online so that there is no specific installation required and can
be used on the fly. Since there are several entities involved, with dynamic changes in stock price, a
distributed system would be the best solution possible for real world implementation. Systems are
distributed over different systems, thus not overloading a server - which is expected in stock market. The
motivation being in real environment, entities involved will be located on separate machines which are
connected over the WAN. They communicate and coordinate with each other by passing messages and
each of them has their own memory. All the entities share a common goal i.e. to enable user to buy/sell his
stocks. The user perceives the entire system as one whole unit and is unaware of where the individual
components in the system are located.

The system is REST based which therefore relies on correct message passing between each of the entities
to complete a successful transaction. JSON serialization technique is used for this purpose. Reason being, it
was easy to understand and was well supported over the frameworks we used. Working in a distributed
system requires services to be available in a registry where it can be discovered and used. This helps mainly
in the scenarios where if the service location changes, the client code does not have to be changed. The
service registry remains same for the client irrespective of the new location of the service. Eureka consists
of a Server, Client, Service and Instance. Eureka server is a discovery server containing a registry of services
and a REST api that can be used to register a service, unregister a service and discover the location of other
services. Eureka services can be defined as any application that can be found in Eureka Server’s registry
and can be discovered by others. Eureka Instance is any application that registers itself with the Eureka
server. Lastly, Eureka client can be defined as any application that can discover other services. Eureka
allows services to find and communicate with each other without hard coding hostname and port. The only
‘fixed point’ in such an architecture consists of service registry with which each service must register.

The Virtual Stock Market Project contains following things:

a. EurekaServer Project

b. StockMarketBank Project

c. StockMarket_CompanyA Project

d. StockMarket_CompanyB Project

e. StockMarket_CompanyC Project

f. StockMarket_SM Project

g. virtaulStockMarketBroker Project



So to run Virtual Stock Market we need to Clone following Repositories from Git:

a. https://github.com/PavanKulhalli/virtaulStockMarketBroker.git

b. https://github.com/chiragpoddar12/StockMarket_CompanyC.git

c. https://github.com/chiragpoddar12/StockMarket_CompanyB.git

d. https://github.com/chiragpoddar12/StockMarket_CompanyA.git

e. https://github.com/chiragpoddar12/StockMarket_SM.git

f. https://github.com/sameltanvi/StockMarketBank.git

g. https://github.com/chiragpoddar12/EurekaServer.git
