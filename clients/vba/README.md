
ActivityInfo VBA Client
=======================

This is the beginning of VBA client for ActivityInfo.org intended to give
advanced the ability to automate data import / export and other routine tasks.

This library, could, for example, help with the bulk transfer of an Access Database 
or complicated Excel sheet to an ActivityInfo database, or to apply a bulk transform
on an existing ActivityInfo database.

To use in an Excel project:

* Download the ActivityInfo.xlam add-in
* Open the Visual Basic Editor from your Workbook (Alt-F11)
* Open the "References" dialog from the Tools menu
* Click "Browse..." and choose the ActivityInfo.xlam file

From here, you can create an ActivityInfo client from your own VBA code:

    Dim Client as ActivityInfoClient
    Set Client = ActivityInfoFactory.CreateClient("myusername@ngo.org", "mypassword")
   
    ' Print a list of my databases
    Dim Database as Database
    For Each Database in Client.QueryDatabases() 
 	    Debug.Print Database.Name
    Next
	

   