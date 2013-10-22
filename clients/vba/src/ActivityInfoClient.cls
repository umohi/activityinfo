VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "ActivityInfoClient"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True

' The User's Email with which to authenticate
Public AccountEmail As String

' The User's ActivityInfo Password
Public Password As String

' The Host (default www.activityinfo.org)
Public Host As String

' The Port to connect to (0=default)
Public Port As Integer

' True to connect with https
Public Secure As Boolean

Private JsonParser As New Json

Private Sub Class_Initialize()
    Host = "www.activityinfo.org"
    Port = 0 ' default
    Secure = True
End Sub

' Return a list of all databases visible to this user.
' If no credentials have been provided, it will
' return a list of public databases
Public Function QueryDatabases() As Collection
    Dim Dbs As New Collection
    Dim Db As Database
    Dim DbJson As Object
    For Each DbJson In Query("resources/databases")
        Set Db = New Database
        Db.Init Me, DbJson
        Dbs.Add Db
    Next
    Set QueryDatabases = Dbs
End Function

Public Function QuerySites(Optional Location, Optional Database, Optional Activity)

    Dim QueryString As String
    QueryString = ""
    QueryString = AppendIds(QueryString, "database", Database)
    QueryString = AppendIds(QueryString, "activity", Activity)
    QueryString = AppendIds(QueryString, "location", Location)
        
    Dim JsonArray As Collection
    Set JsonArray = Query("resources/sites" & QueryString)
    
        Set QuerySites = New Collection
    Dim SiteJson As Object
    Dim Site As Site
    For Each SiteJson In JsonArray
        Set Site = New Site
        Site.Init Me, SiteJson
        QuerySites.Add Site
    Next
End Function

Public Function QueryCountry(CountryCode As String) As Country
    
    Dim Json As Object
    Set Json = Query("resources/country/" + CountryCode)
    
    Dim Country As New Country
    Country.Init Me, Json
    
    Set QueryCountry = Country
End Function

Private Function AppendIds(QueryString As String, Dimension As String, Ids As Variant) As String
    AppendIds = QueryString
    If IsNumeric(Ids) Then
        If Len(AppendIds) = 0 Then
            AppendIds = "?"
        Else
            AppendIds = AppendIds & "&"
        End If
        AppendIds = AppendIds & Dimension & "=" & Ids
    End If
End Function

' Queries a URL with Get
Public Function Query(Uri As String) As Object
    Dim XMLhttp As Object
    Set XMLhttp = CreateObject("msxml2.xmlhttp")

    XMLhttp.Open "GET", BaseURI & Uri, False
    
    ' Tell the server we want the response in JSON
    XMLhttp.SetRequestHeader "Accept", "application/json"
    
    ' if credentials have been provided, send using
    ' basic authentication
    If Len(AccountEmail) > 0 Then
        XMLhttp.SetRequestHeader "Authorization", "Basic " & EncodeBase64(AccountEmail & ":" & Password)
    End If
    
    XMLhttp.Send
    
    ' check the status code of the request, which should be
    ' in the 2xx range signaling success
    If XMLhttp.Status < 200 Or XMLhttp.Status >= 300 Then
        Err.Raise XMLhttp.Status, "ActivityInfo.Client", "Error querying " & Uri
    End If
    
    Dim Json As String
    Let Json = XMLhttp.responsetext
    
    Debug.Print Json
    
    Set Query = JsonParser.Parse(Json)
End Function

' Executes a RPC Command
'
' See link below for all supported Commands
' https://github.com/bedatadriven/activityinfo/tree/maintenance/server/src/main/java/org/activityinfo/shared/command
Public Function Execute(Command As Command) As Object

    Dim CommandJson As Object
    Set CommandJson = CreateObject("Scripting.Dictionary")
    CommandJson.Add "type", Command.GetType
    CommandJson.Add "command", Command.ToJson
    
    Dim XMLhttp As Object
    Set XMLhttp = CreateObject("msxml2.xmlhttp")

    XMLhttp.Open "POST", BaseURI & "command", False
    
    ' if credentials have been provided, send using
    ' basic authentication
    If Len(AccountEmail) > 0 Then
        XMLhttp.SetRequestHeader "Authorization", "Basic " & EncodeBase64(AccountEmail & ":" & Password)
    End If
    XMLhttp.SetRequestHeader "Content-Type", "application/json"
    XMLhttp.Send (JsonParser.ToJson(CommandJson))

    ' check the status code of the request, which should be
    ' in the 2xx range signaling success
    If XMLhttp.Status < 200 Or XMLhttp.Status >= 300 Then
        Err.Raise XMLhttp.Status, "ActivityInfo.Client", "Error executing command " & CommandType
    End If
End Function

Private Function BaseURI() As String
    If Secure Then
        BaseURI = "https://"
    Else
        BaseURI = "http://"
    End If
    BaseURI = BaseURI & Host
    If Port <> 0 Then
        BaseURI = BaseURI & ":" & Port
    End If
    BaseURI = BaseURI & "/"
End Function

' Encodes a string in Base64, required for basic authentication
' See http://en.wikipedia.org/wiki/Basic_access_authentication
Private Function EncodeBase64(text As String) As String
    Dim arrData() As Byte
    arrData = StrConv(text, vbFromUnicode)
    
    Dim doc As Object
    Set doc = CreateObject("MSXML2.DOMDocument")
    
    Dim node As Object
    Set node = doc.createElement("b64")
    
    node.DataType = "bin.base64"
    node.nodeTypedValue = arrData
    EncodeBase64 = node.text
End Function

