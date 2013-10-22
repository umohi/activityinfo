VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "AdminEntity"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private MyOwner As ActivityInfoClient
Private MyId As Long
Private MyParentId As Long
Private MyName As String
Private MyLevel As AdminLevel
Private MyParent As AdminEntity

Public Sub Init(Owner As ActivityInfoClient, Level As AdminLevel, Json As Object)
    Set MyOwner = Owner
    Set MyLevel = Level
    MyId = Json.Item("id")
    MyName = Json.Item("name")
    If IsNumeric(Json.Item("parentId")) Then
        MyParentId = Json.Item("parentId")
    Else
        MyParentId = -1
    End If
End Sub

Public Property Get IsRoot() As Boolean
    IsRoot = (MyParentId = -1)
End Property

Public Property Get Parent() As AdminEntity
    If MyParentId <> -1 And MyParent Is Nothing Then
        Set MyParent = Level.Parent.LookupById(MyParentId)
    End If
    Set Parent = MyParent
End Property

Public Property Get Name() As String
    Name = MyName
End Property

Public Property Get Id() As Long
    Id = MyId
End Property

Public Property Get Level() As AdminLevel
    Set Level = MyLevel
End Property

