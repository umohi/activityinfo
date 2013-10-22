VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Activity"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
Private MyOwner As ActivityInfoClient
Private MyId As Long
Private MyName As String


Public Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    Let MyId = Json.Item("id")
    Let MyName = Json.Item("name")
    ' Todo: parse other attributes
End Sub


Public Property Get Id() As Long
    Id = MyId
End Property

Public Property Get Name() As String
    Name = MyName
End Property


