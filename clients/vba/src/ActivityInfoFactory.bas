Attribute VB_Name = "ActivityInfoFactory"

Public Function CreateClient(Email As String, Password As String) As ActivityInfoClient
    Dim Client As New ActivityInfoClient
    Client.AccountEmail = Email
    Client.Password = Password
    Set CreateClient = Client
End Function


Public Function NewCreateLocationCommand(LocationTypeId As Long) As CreateLocationCommand
    Set NewCreateLocationCommand = New CreateLocationCommand
    NewCreateLocationCommand.LocationTypeId = LocationTypeId
End Function

Public Function GenerateId() As Long
    Dim KeyMax As Long
    Dim KeyMin As Long
    KeyMax = 2 ^ 31
    KeyMin = 2 ^ 10
    
    GenerateId = CLng(KeyMin + (Rnd() * (KeyMax - KeyMin)))
End Function


