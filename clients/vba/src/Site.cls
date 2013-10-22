VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Site"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private MyId As Long
Private MyOwner As ActivityInfoClient
Private MyActivityId As Integer
Private MyPartner As Partner
Private MyLocation As Location

Sub Init(Owner As ActivityInfoClient, Json As Object)
    Set MyOwner = Owner
    Let MyId = Json.Item("id")
    Let MyActivityId = Json.Item("activityId")
    
    Set MyPartner = New Partner
    MyPartner.Init Owner, Json.Item("partner")
    
    Set MyLocation = New Location
    MyLocation.Init Owner, Json.Item("location")
End Sub

Public Property Get Location() As Location
    Set Location = MyLocation
End Property

Public Property Get Id() As Long
    Id = MyId
End Property

Public Property Get Partner() As Partner
    Set Partner = Partner
End Property

' Changes the location of the site to the site
' with the new id
Public Sub ChangeLocation(NewLocationId As Long)

    Dim Command As New UpdateSiteCommand
    Command.SiteId = Id
    Command.NewLocationId = NewLocationId
    MyOwner.Execute Command
   
    Dim NewLocationObj As Object
    Set NewLocationObj = CreateObject("Scripting.Dictionary")
    NewLocationObj.Add key:="id", Item:=NewLocationId
    
    Set MyLocation = New Location
    MyLocation.Init MyOwner, NewLocationObj
End Sub

