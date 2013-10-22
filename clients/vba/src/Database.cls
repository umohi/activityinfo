VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "Database"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

Private Owner As ActivityInfoClient
Private MyId As Long

Public Name As String

' Use lazy loading to load more information on demand
Private SchemaLoaded As Boolean

Private MyCountry As Country
Private MyPartners As Collection
Private MyActivities As Collection

Sub Init(Client As ActivityInfoClient, Json As Object)
    Set Owner = Client
    MyId = Json.Item("id")
    Name = Json.Item("name")
End Sub

Public Property Get Id() As Long
    Id = MyId
End Property


Public Property Get Partners() As Collection
    EnsureSchemaLoaded
    Set Partners = MyPartners
End Property
    
Private Sub EnsureSchemaLoaded()
    If Not SchemaLoaded Then
        Dim Schema As Object
        Set Schema = Owner.Query("resources/database/" & Id & "/schema")
        
        Set MyCountry = New Country
        MyCountry.Init Owner, Schema.Item("country")
        
        Set MyPartners = New Collection
        Dim Partner As Partner
        Dim PartnerJson As Object
        For Each PartnerJson In Schema.Item("partners")
            Set Partner = New Partner
            Partner.Init Owner, PartnerJson
            MyPartners.Add Partner
        Next
        
        Set MyActivities = New Collection
        Dim Activity As Activity
        Dim ActivityJson As Object
        For Each ActivityJson In Schema.Item("activities")
            Set Activity = New Activity
            Activity.Init Owner, ActivityJson
            MyActivities.Add Activity
        Next
        SchemaLoaded = True
    End If
End Sub

