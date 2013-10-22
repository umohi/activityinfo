Attribute VB_Name = "Tests"

Public Sub TestQueryDatabases()

    Dim Client As New ActivityInfoClient
    
    Dim Dbs As Collection
    Set Dbs = Client.QueryDatabases()
    
    Dim Db As Database
    For Each Db In Dbs
        Debug.Print Db.Name
    Next
    
    Set Db = Dbs.Item(1)
    
    Dim Partner As Partner
    For Each Partner In Db.Partners
        Debug.Print Partner.Name
    Next
End Sub
