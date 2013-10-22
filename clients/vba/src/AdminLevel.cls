VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "AdminLevel"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private MyOwner As ActivityInfoClient
Private MyId As Long
Private MyName As String
Private MyEntities As Collection
Private MyParent As AdminLevel

Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    MyId = Json.Item("id")
    MyName = Json.Item("name")
End Sub

Sub InitParent(Parent As AdminLevel)
    Set MyParent = Parent
End Sub

Public Property Get Id() As Long
    Id = MyId
End Property

Public Property Get Name() As String
    Name = MyName
End Property

Public Function LookupByName(Name As String) As AdminEntity
    EnsureEntitiesLoaded
    Set LookupByName = MyEntities(Name)
End Function

Public Function LookupById(Id As Long) As AdminEntity
    EnsureEntitiesLoaded
    
    Dim Entity As AdminEntity
    For Each Entity In MyEntities
        If Entity.Id = Id Then
            Set LookupById = Entity
            Exit Function
        End If
    Next
    
    Err.Raise 404, "AdminLevel", "No entity with id " & Id & " in level " & Name
End Function

Public Property Get Parent() As AdminLevel
    Set Parent = MyParent
End Property

Private Sub EnsureEntitiesLoaded()
    If MyEntities Is Nothing Then
        Set MyEntities = New Collection
        
        Dim JsonArray As Collection
        Set JsonArray = MyOwner.Query("resources/adminLevel/" & MyId & "/entities")
        Dim JsonObject As Object
        For Each JsonObject In JsonArray
            
            Dim AdminEntity As AdminEntity
            Set AdminEntity = New AdminEntity
            AdminEntity.Init MyOwner, Me, JsonObject
            
            MyEntities.Add AdminEntity, AdminEntity.Name
        Next
    End If
End Sub
