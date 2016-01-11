Hand Written Equation Solver

To Create DataSet from Images:
	- Go to CreateDataset directory
	- run runme.m
	- The resulted .mat files will appear in the same diractory.
	- To change the No. of basis used, Change the value of K at the end of data123.m file.
	
For Recognition:
	- Make sure have stated the dataset path in the getchar.m 
	- Also add the path in matlab for the dataset folder and subfolders
	- Run runme.m
	- The program will promt a browser window asking for the handwritten equation directory.
	- Make sure the hand written equation is already extracted with Tesseract OCR.( Run OCR jar file and extract the equation from image)
	- Now for the final result
	- if perfect recognition happens, then type ans in cmd line to view the solution else an error will be show for imperfect recognition.
	- While running OCR make sure that the extracted images dimensions are 30 x 30
	- Note:
			- 25k L Data means 25 basis Large(80 char) Dataset
			- 8k data  means 8 basis small(10) dataset