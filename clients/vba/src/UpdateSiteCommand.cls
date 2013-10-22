VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "UpdateSiteCommand"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
Implements Command

Public SiteId As Long
Public NewLocationId As Long

Private Function Command_GetType() As String
    Command_GetType = "UpdateSite"
End Function


Private Function Command_ToJson() As Object
    Dim Changes As Object
    Set Changes = CreateObject("Scripting.Dictionary")
    Changes.Add "locationId", NewLocationId
    
    Dim Update As Object
    Set Update = CreateObject("Scripting.Dictionary")
    Update.Add "siteId", SiteId
    Update.Add "changes", Changes
    
    Set Command_ToJson = Update
    
    
End Function
