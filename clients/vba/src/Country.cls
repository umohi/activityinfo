VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Country"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private MyOwner As ActivityInfoClient
Private MyId As Long
Private MyName As String
Private MyAdminLevels As Collection

Public Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    Let MyId = Json.Item("id")
    Let MyName = Json.Item("name")
End Sub

' ActivityInfo's internal Id for this country
Public Property Get Id() As Long
    Id = MyId
End Property

' This Country's Name
Public Property Get Name() As String
    Name = MyName
End Property

Public Property Get AdminLevels() As Collection
    EnsureLevelsLoaded
    Set AdminLevels = MyAdminLevels
End Property

Private Sub EnsureLevelsLoaded()
    If MyAdminLevels Is Nothing Then
        Set MyAdminLevels = New Collection
    
        Dim JsonArray As Collection
        Set JsonArray = MyOwner.Query("resources/country/" & MyId & "/adminLevels")
        
        Dim JsonObject As Object
        For Each JsonObject In JsonArray
            Dim Level As AdminLevel
            Set Level = New AdminLevel
            Level.Init MyOwner, JsonObject
            MyAdminLevels.Add Level, key:=Level.Name
        Next
        
        ' now link parents
        For Each JsonObject In JsonArray
            If IsNumeric(JsonObject.Item("parentId")) Then
                Dim LevelId As Long
                Let LevelId = JsonObject.Item("id")
                
                Dim ParentId As Long
                Let ParentId = JsonObject.Item("parentId")
                
                LevelById(LevelId).InitParent LevelById(ParentId)
            End If
        Next
    End If
End Sub

Public Function LevelById(Id As Long) As AdminLevel
    EnsureLevelsLoaded
    
    Dim Level As AdminLevel
    For Each Level In AdminLevels
        If Level.Id = Id Then
            Set LevelById = Level
            Exit Function
        End If
    Next
    
    Err.Raise 404, "Country", "No level with id " & Id & " in country " & Name
End Function

