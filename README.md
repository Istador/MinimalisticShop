# English

Minimalistic Shop is a website, that was created in the summer semester 2013 at the [Relational Databases](https://rcl.blackpinguin.de/haw/bms/13ss/RDB/) course of [B.Sc. Media Systems (B-MS)](https://rcl.blackpinguin.de/haw/bms/).

This technical demonstration of a online marketplace offers various product categories for purchase. Products that are added to the shopping cart can be ordered after registering or loging into an user account. The product categories are arranged hierarchicallly, products can be in several categories, and the search can be limited to a specific category.

The task specification for the project required it to use Java classes, JDBC, Tomcat and JSP. Instead of Java, I've used Scala to generate compiled Java classes. The JSP files don't contain any application logic, but each JSP calls a single Scala method with the parameters of the HTTP connection.

The architecture of the Scala project is structured into three packages. One package for the database access via prepared statemens, the business logic and transactions, another package for the XML output generation, and the last package to validate the input parameters and act according to them. The XML that the website provides is parsed to HTML with XSLT 1.0 by the client's browser.

The Minimalistic Shop is available [here](https://rdb.blackpinguin.de/).

Since I moved the site from another server, an unknown error with the server cache occurs sometimes, which I haven't investigated yet. It should only occur once per visit and is gone when the desired page is requested again.

|                  |                                                                                               |
| ---------------- | --------------------------------------------------------------------------------------------- |
| __Languages__    | Scala, SQL, XML, XSLT, HTML, CSS, JavaScript, Java[1]                                         |
| __Technologies__ | XSD, DTD, MySQL, Prepared Statements, JDBC, bcrypt, Mersenne Twister, BigDecimal, Tomcat, JSP |
| __IDE__          | Eclipse with Scala IDE                                                                        |
| __Participants__ | 1                                                                                             |

### Footnotes

- [1]	The only usage of Java source code in this project is in the JSP files and in reused bcrypt and Mersenne Twister classes from one of my older projects.

# Deutsch

Minimalistic Shop ist eine Webseite, die im Sommersemester 2013 im Modul [Relationale Datenbanken](https://rcl.blackpinguin.de/haw/bms/13ss/RDB/de.html) des Studienganges [Bachelor Media Systems (B-MS)](https://rcl.blackpinguin.de/haw/bms/de.html) entstanden ist.

In dieser technischen Demonstration eines Online-Shops werden Produkte verschiedenster Kategorien zum Kauf angeboten. Artikel können zum Warenkorb hinzugefügt werden und nach der Registrierung eines neuen Benutzeraccounts bzw. nach dem Login bestellt werden. Die Produktkategorien sind hierarchisch gegliedert, Produkte können in mehreren Kategorien stehen, und die Suche lässt sich auf bestimmte Kategorien eingrenzen.

Vorgabe für das Projekt war es, Java-Klassen, JDBC, Tomcat und JSP zu verwenden. Statt Java habe ich Scala verwendet, was zu Java-Klassen kompiliert wird. Die JSP-Dateien enthalten keine Anwendungslogik, sondern rufen lediglich jeweils eine Scala-Methode mit den Parametern der HTTP-Verbindung auf.

Aufgeteilt ist die Architektur des Scala-Projektes in drei Pakete. Ein Paket für den Datenbankzugriff mittels Prepared Statements, Geschäftslogik und Transaktionen, eines für die XML-Ausgabegenerierung und das letzte Paket für die Validierung der Eingabeparameter. Das XML, welches die Webseite ausliefert, wird vom Browser des Besuchers mittels XSLT 1.0 zu HTML geparst.

Erreichbar ist der Minimalistic Shop [hier](https://rdb.blackpinguin.de/).

Seit dem Umzug von einem anderen Server tritt gelegentlich ein unbekannter Fehler im Zusammenhang mit dem Server-Cache auf, den ich mir bisher noch nicht genauer angeschaut habe. Der Fehler dürfte pro Besuch nur einmalig auftauchen, dann einfach die gewünschte Seite erneut aufrufen.

|                  |                                                                                               |
| ---------------- | --------------------------------------------------------------------------------------------- |
| __Sprachen__     | Scala, SQL, XML, XSLT, HTML, CSS, JavaScript, Java[1]                                         |
| __Technologien__ | XSD, DTD, MySQL, Prepared Statements, JDBC, bcrypt, Mersenne Twister, BigDecimal, Tomcat, JSP |
| __IDE__          | Eclipse mit Scala IDE                                                                         |
| __Beteiligte__   | 1                                                                                             |

### Fußnoten

- [1]	Die einzige Verwendung von Java-Quelltext in diesem Projekt ist in den JSP-Dateien und in den kopierten Klassen für bcrypt und Mersenne-Twister von einem älteren Projekt von mir.
