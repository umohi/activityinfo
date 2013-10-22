VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "JSON"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
' Taken from https://code.google.com/p/vba-json/


Private Sub Class_Initialize()

End Sub

Private Sub Class_Terminate()

End Sub

'
'   parse string and create JSON object (Dictionary or Collection in VB)
'
Public Function Parse(ByRef str As String) As Object

    Dim index As Long
    index = 1
    
    On Error Resume Next

    Call skipChar(str, index)
    Select Case Mid(str, index, 1)
    Case "{"
        Set Parse = parseObject(str, index)
    Case "["
        Set Parse = parseArray(str, index)
    End Select

End Function

'
'   parse collection of key/value (Dictionary in VB)
'
Private Function parseObject(ByRef str As String, ByRef index As Long) As Object

    Set parseObject = CreateObject("Scripting.Dictionary")
    
    ' "{"
    Call skipChar(str, index)
    If Mid(str, index, 1) <> "{" Then Err.Raise vbObjectError + INVALID_OBJECT, Description:="char " & index & " : " & Mid(str, index)
    index = index + 1
    
    Do
    
        Call skipChar(str, index)
        If "}" = Mid(str, index, 1) Then
            index = index + 1
            Exit Do
        ElseIf "," = Mid(str, index, 1) Then
            index = index + 1
            Call skipChar(str, index)
        End If
        
        Dim key As String
        
        ' add key/value pair
        parseObject.Add key:=parseKey(str, index), Item:=parseValue(str, index)
        
    Loop

End Function

'
'   parse list (Collection in VB)
'
Private Function parseArray(ByRef str As String, ByRef index As Long) As Collection

    Set parseArray = New Collection
    
    ' "["
    Call skipChar(str, index)
    If Mid(str, index, 1) <> "[" Then Err.Raise vbObjectError + INVALID_ARRAY, Description:="char " & index & " : " + Mid(str, index)
    index = index + 1
    
    Do
        
        Call skipChar(str, index)
        If "]" = Mid(str, index, 1) Then
            index = index + 1
            Exit Do
        ElseIf "," = Mid(str, index, 1) Then
            index = index + 1
            Call skipChar(str, index)
        End If
        
        ' add value
        parseArray.Add parseValue(str, index)
        
    Loop

End Function

'
'   parse string / number / object / array / true / false / null
'
Private Function parseValue(ByRef str As String, ByRef index As Long)

    Call skipChar(str, index)
    
    Select Case Mid(str, index, 1)
    Case "{"
        Set parseValue = parseObject(str, index)
    Case "["
        Set parseValue = parseArray(str, index)
    Case """", "'"
        parseValue = parseString(str, index)
    Case "t", "f"
        parseValue = parseBoolean(str, index)
    Case "n"
        parseValue = parseNull(str, index)
    Case Else
        parseValue = parseNumber(str, index)
    End Select

End Function

'
'   parse string
'
Private Function parseString(ByRef str As String, ByRef index As Long) As String

    Dim quote   As String
    Dim char    As String
    Dim Code    As String
    
    Call skipChar(str, index)
    quote = Mid(str, index, 1)
    index = index + 1
    Do While index > 0 And index <= Len(str)
        char = Mid(str, index, 1)
        Select Case (char)
        Case "\"
            index = index + 1
            char = Mid(str, index, 1)
            Select Case (char)
            Case """", "\\", "/"
                parseString = parseString & char
                index = index + 1
            Case "b"
                parseString = parseString & vbBack
                index = index + 1
            Case "f"
                parseString = parseString & vbFormFeed
                index = index + 1
            Case "n"
                parseString = parseString & vbNewLine
                index = index + 1
            Case "r"
                parseString = parseString & vbCr
                index = index + 1
            Case "t"
                parseString = parseString & vbTab
                index = index + 1
            Case "u"
                index = index + 1
                Code = Mid(str, index, 4)
                parseString = parseString & ChrW(Val("&h" + Code))
                index = index + 4
            End Select
        Case quote
            index = index + 1
            Exit Function
        Case Else
            parseString = parseString & char
            index = index + 1
        End Select
    Loop

End Function

'
'   parse number
'
Private Function parseNumber(ByRef str As String, ByRef index As Long)

    Dim value   As String
    Dim char    As String
    
    Call skipChar(str, index)
    Do While index > 0 And index <= Len(str)
        char = Mid(str, index, 1)
        If InStr("+-0123456789.eE", char) Then
            value = value & char
            index = index + 1
        Else
            'some parts commented out here. It incorrectly converted to Int, which caused errors for high numbers
            'If InStr(value, ".") Or InStr(value, "e") Or InStr(value, "E") Then
                parseNumber = CDbl(value)
            'Else
            '    parseNumber = CInt(value)
            'End If
            Exit Function
        End If
    Loop


End Function

'
'   parse true / false
'
Private Function parseBoolean(ByRef str As String, ByRef index As Long) As Boolean

    Call skipChar(str, index)
    If Mid(str, index, 4) = "true" Then
        parseBoolean = True
        index = index + 4
    ElseIf Mid(str, index, 5) = "false" Then
        parseBoolean = False
        index = index + 5
    Else
        Err.Raise vbObjectError + INVALID_BOOLEAN, Description:="char " & index & " : " & Mid(str, index)
    End If

End Function

'
'   parse null
'
Private Function parseNull(ByRef str As String, ByRef index As Long)

    Call skipChar(str, index)
    If Mid(str, index, 4) = "null" Then
        parseNull = Null
        index = index + 4
    Else
        Err.Raise vbObjectError + INVALID_NULL, Description:="char " & index & " : " & Mid(str, index)
    End If

End Function

Private Function parseKey(ByRef str As String, ByRef index As Long) As String

    Dim dquote  As Boolean
    Dim squote  As Boolean
    Dim char    As String
    
    Call skipChar(str, index)
    Do While index > 0 And index <= Len(str)
        char = Mid(str, index, 1)
        Select Case (char)
        Case """"
            dquote = Not dquote
            index = index + 1
            If Not dquote Then
                Call skipChar(str, index)
                If Mid(str, index, 1) <> ":" Then
                    Err.Raise vbObjectError + INVALID_KEY, Description:="char " & index & " : " & parseKey
                End If
            End If
        Case "'"
            squote = Not squote
            index = index + 1
            If Not squote Then
                Call skipChar(str, index)
                If Mid(str, index, 1) <> ":" Then
                    Err.Raise vbObjectError + INVALID_KEY, Description:="char " & index & " : " & parseKey
                End If
            End If
        Case ":"
            If Not dquote And Not squote Then
                index = index + 1
                Exit Do
            End If
        Case Else
            If InStr(vbCrLf & vbCr & vbLf & vbTab & " ", char) Then
            Else
                parseKey = parseKey & char
            End If
            index = index + 1
        End Select
    Loop

End Function

'
'   skip special character
'
Private Sub skipChar(ByRef str As String, ByRef index As Long)

    While index > 0 And index <= Len(str) And InStr(vbCrLf & vbCr & vbLf & vbTab & " ", Mid(str, index, 1))
        index = index + 1
    Wend

End Sub

Public Function ToJson(ByRef obj As Variant) As String

    Select Case VarType(obj)
        Case vbNull
            ToJson = "null"
        Case vbDate
            ToJson = """" & CStr(obj) & """"
        Case vbString
            ToJson = """" & encode(obj) & """"
        Case vbObject
            Dim bFI, i
            bFI = True
            If TypeName(obj) = "Dictionary" Then
                ToJson = ToJson & "{"
                Dim keys
                keys = obj.keys
                For i = 0 To obj.Count - 1
                    If bFI Then bFI = False Else ToJson = ToJson & ","
                    Dim key
                    key = keys(i)
                    ToJson = ToJson & """" & key & """:" & ToJson(obj(key))
                Next i
                ToJson = ToJson & "}"
            ElseIf TypeName(obj) = "Collection" Then
                ToJson = ToJson & "["
                Dim value
                For Each value In obj
                    If bFI Then bFI = False Else ToJson = ToJson & ","
                    ToJson = ToJson & ToJson(value)
                Next value
                ToJson = ToJson & "]"
            End If
        Case vbBoolean
            If obj Then ToJson = "true" Else ToJson = "false"
        Case vbVariant, vbArray, vbArray + vbVariant
            Dim sEB
            Dim NoDims As Long
            ToJson = multiArray(obj)
        Case Else
            ToJson = Replace(obj, ",", ".")
    End Select

End Function

Private Function encode(str) As String
    
    Dim i, j, aL1, aL2, c, p

    aL1 = Array(&H22, &H5C, &H2F, &H8, &HC, &HA, &HD, &H9)
    aL2 = Array(&H22, &H5C, &H2F, &H62, &H66, &H6E, &H72, &H74)
    For i = 1 To Len(str)
        p = True
        c = Mid(str, i, 1)
        For j = 0 To 7
            If c = Chr(aL1(j)) Then
                encode = encode & "\" & Chr(aL2(j))
                p = False
                Exit For
            End If
        Next

        If p Then
            Dim a
            a = AscW(c)
            If a > 31 And a < 127 Then
                encode = encode & c
            ElseIf a > -1 Or a < 65535 Then
                encode = encode & "\u" & String(4 - Len(Hex(a)), "0") & Hex(a)
            End If
        End If
    Next
End Function

Private Function multiArray(aBD)  ' Array BoDy, Integer BaseCount, String PoSition
    Dim NoDimensions
    Dim i1 As Long, i2 As Long
    Dim DimList(1 To 10) As Long
    
    NoDimensions = HowManyDimensions(aBD)
    Select Case NoDimensions
        Case 1
            multiArray = multiArray & "["
            For i1 = LBound(aBD, 1) To UBound(aBD, 1)
                multiArray = multiArray & ToJson(aBD(i1))
                If i1 < UBound(aBD, 1) Then multiArray = multiArray & ","
            Next i1
            multiArray = multiArray & "]"
        Case 2
            multiArray = multiArray & "["
            For i1 = LBound(aBD, 1) To UBound(aBD, 1)
                multiArray = multiArray & "["
                For i2 = LBound(aBD, 2) To UBound(aBD, 2)
                    multiArray = multiArray & ToJson(aBD(i1, i2))
                    If i2 < UBound(aBD, 2) Then multiArray = multiArray & ","
                Next i2
                multiArray = multiArray & "]"
                If i1 < UBound(aBD, 1) Then multiArray = multiArray & ","
            Next i1
            multiArray = multiArray & "]"
        Case Else
            'Not much point in doing anything here. The error would just leave it blank.
    End Select
End Function

Private Function HowManyDimensions(AnArray) As Long
    'find number of dimensions
    On Error GoTo DimensionOverflow
    Dim ErrorCheck As Long, DimensionNumber As Long
    For DimensionNumber = 1 To 60001
        ErrorCheck = LBound(AnArray, DimensionNumber)
    Next DimensionNumber

DimensionOverflow:
    HowManyDimensions = DimensionNumber - 1
End Function
