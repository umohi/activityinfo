VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Location"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private MyOwner As ActivityInfoClient
Private MyId As Long
Private MyName As String
Private MyLongitude As Double
Private MyLatitude As Double

Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    MyId = Json.Item("id")
    MyName = Json.Item("name")
    MyLongitude = Json.Item("longitude")
    MyLatitude = Json.Item("latitude")
End Sub

Public Property Get Id() As Long
    Id = MyId
End Property

Public Property Get Name() As String
    Name = MyName
End Property

Public Property Get Longitude() As Double
    Longitude = MyLongitude
End Property

Public Property Get Latitude() As Double
    Latitude = MyLatitude
End Property
