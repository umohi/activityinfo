option explicit

Const vbext_ct_ClassModule = 2
Const vbext_ct_Document = 100
Const vbext_ct_MSForm = 3
Const vbext_ct_StdModule = 1

Main

Sub Main
    Dim xl
    Dim fs
    Dim WBook
    Dim VBComp
    Dim Sfx
    Dim ExportFolder

	Set xl = CreateObject("Excel.Application")
	Set fs = CreateObject("Scripting.FileSystemObject")

	xl.Visible = true

	Set WBook = xl.Workbooks.Open("f:\dev\activityinfo\clients\vba\ActivityInfo.xlam")

	ExportFolder = WBook.Path & "\src"
	
	fs.CreateFolder(ExportFolder)

	For Each VBComp In WBook.VBProject.VBComponents
		Select Case VBComp.Type
			Case vbext_ct_ClassModule, vbext_ct_Document
				Sfx = ".cls"
			Case vbext_ct_MSForm
				Sfx = ".frm"
			Case vbext_ct_StdModule
				Sfx = ".bas"
			Case Else
				Sfx = ""
		End  Select
		If Sfx <> "" Then
			On Error Resume Next
			Err.Clear
			VBComp.Export ExportFolder & "\" & VBComp.Name & Sfx
			If Err.Number <> 0 Then
				MsgBox "Failed to export " & ExportFolder & "\" & VBComp.Name & Sfx
			End If
			On Error Goto 0
		End If
	Next

	xl.Quit
End Sub