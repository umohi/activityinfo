VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "CreateLocationCommand"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
Implements Command

Public LocationId As Long
Public LocationTypeId As Long
Public Longitude As Variant
Public Latitude As Variant
Public Name As String
Public AdminEntities As New Collection

Private Function Command_GetType() As String
    Command_GetType = "CreateLocation"
End Function

Private Function Command_ToJson() As Object
    If IsMissing(LocationId) Then
        LocationId = GenerateId()
    End If
                       
    Dim Properties As Object
    Set Properties = CreateObject("Scripting.Dictionary")
    Properties.Add "id", LocationId
    Properties.Add "name", Name
    Properties.Add "locationTypeId", LocationTypeId
    If (IsNumeric(Latitude) And IsNumeric(Longitude)) Then
        Properties.Add "latitude", Latitude
        Properties.Add "longitude", Longitude
    End If
    
    Dim Entity As AdminEntity
    For Each Entity In AdminEntities
        AddEntityRecursive Properties, Entity
    Next
    
    Dim Command As Object
    Set Command = CreateObject("Scripting.Dictionary")
    Command.Add "properties", Properties
               
    Set Command_ToJson = Command
End Function

' Adds an entity and all of its ancestors to the location properties
Private Function AddEntityRecursive(Properties As Object, Entity As AdminEntity)
    Properties.Add "E" & Entity.Level.Id, Entity.Id
    If Not Entity.IsRoot Then
        AddEntityRecursive Properties, Entity.Parent
    End If
End Function
