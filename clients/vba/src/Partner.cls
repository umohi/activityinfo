VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Partner"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
Option Explicit

Private MyOwner As ActivityInfoClient
Private MyId As Long

Public Name As String
Public FullName As String

Public Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    MyId = Json.Item("id")
    Name = Json.Item("name")
    FullName = Json.Item("fullName")
End Sub

Public Property Get Id() As Long
    Id = MyId
End Property

 
