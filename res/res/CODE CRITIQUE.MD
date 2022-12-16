## Code Critique:

### Design Critique:
* The code follows a proper, strict design of the MVC design and all the components are properly
coupled and decoupled with each other where it is necessary. 
* For the controller, the code does follow a good use of the 'Feature Design Pattern'. 
* While the code also lacks the 'Command Design Pattern' inside the controller for the text-based 
controller where all the methods are just in one common "TextController" class which makes the 
process of adding/making any new changes to the code quite painstaking.

### Implementation Critique:
* When it comes to implementation, the code implements all the requirements quite effectively. 
* One drawback is that the code has a default commission price of $5.00 by default without setting
it up manually by the user while the user initially try to device a simple or Dollar Cost Averaging
strategies.
* Another issue was faced with the date implementation in the code for CLI-based view. The model was
throwing an error of invalid date format even when a correct format was provided which was solved 
by the receivers of the code in order to carry out a transaction. The mode for 
'formatter.withResolverStyle' in the 'LocalDate.parse' was set to 'STRICT' which was causing this
error.
* The code caches all the portfolios and stores it and displays it every time with an index which
gives user a sense of choice and provides them a view of all the portfolios whenever they try to
carry out a transaction which is a good implementation.

### Documentation Critique:
* The code was documented in a good manner if not perfectly as there were some methods that missed
declaring the **_@params_** and **_@throw_** for the exceptions. Moreover, the code did cross a limit of 100
columns which threw a JavaStyle error on the server for us. We had to solve these errors in order
to properly document the project.
* The 'DESIGN-README' and 'README' were properly documented which helped us to adapt to the code
structure instantaneously and gave us a good walk through of the code.

### Design/Code Strengths:
* The 'AbstractPortfolio' class implements the 'Portfolio' interface, and it is further extended to 
'InFlexiblePortfolio' and 'FlexiblePortfolio' classes. This helps in adding a new type of portfolio
without any hassle and also new changes can be made into the existing classes without making any 
major changes to the parent class.
* The 'AbstractPriceProvider' class implements the 'PriceProvider' interface that basically carries
out the work of calling the AlphaVantage API and fetch the data from the API. This helps if we want
to support a new API for the existing code as it will be done with minimum changes.
* All the fields declared are private which maintains the scope of those fields, and they can be 
only accessed within the class.

### Design/Code Limitations:
* Initially there was an error with all the imports, and they were not imported properly which
  showed error all over the project. We had to get over a call with the code providers in order to
  fix that. 
* Moreover, in the View classes, all imports were made for .awt and .swing libraries. Which we had
  to change to importing only particular files. 
* The classes inside the 'entities' directory are orphan and no parent interface is present for 
them. They are utilized in the program but without any prior declaration in any interface or parent
class. We would have supported them with a parent class/interface.
* The ActionListener for the 'FrameController' is implemented separately in a different class 
'ButtonListener' which is just used once for 'FrameController'. It can be just declared inside that 
class instead of increasing the size of the project.
* The GUI is implemented in a complicated manner when it comes to providing an input to a textfield
in GUI. We would have created separate text-boxes for the same instead of taking input in the table
itself.