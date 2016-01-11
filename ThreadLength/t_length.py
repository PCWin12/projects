from __future__ import with_statement
from com.xhaus.jyson import JysonCodec as json
import math
import csv
import sys
import java.lang.StringBuffer as StringBuffer
import java.lang.Boolean as Boolean
import java.io.FileReader as FileReader
import weka.core.Instances as Instances
import weka.classifiers.functions.LinearRegression as LR
import weka.classifiers.Evaluation as Evaluation
import weka.core.Range as Range
import copy
import os

## Reading Conf File

with open("manifest.json") as man:
	conf = man.read()
config = json.loads(conf)
data_files = config[0]['data_file']
folds = config[0]['folds']
step_size = config[0]['step']
top_n = config[0]['top_n']



for data_file in data_files:
	directory = ''.join(['results/',data_file , '/'])
	if not os.path.exists(directory):
		os.makedirs(directory)

	## Reading Data File
	data_start=0
	thread_array=[];
	with open(data_file) as f:
		for line in f:
			if data_start == 1:
				thread_array.append(int(line.split(',')[-1].replace("\n","")))

			if line[0:5] == '@DATA':
				data_start=1
	thread_array = sorted(thread_array)
	n=len(thread_array)

	## Creating percentiles
	percentiles=[]

	for i in range(0,100,step_size):
		index = int (n*i/100)
		percentiles.append(thread_array[index]) # 0-99 percentile
	percentiles.append(thread_array[n-1]) # 100th percentile

	f = open(''.join([directory , 'percentiles.txt']), 'w')
	f.write(str(percentiles))
	f.close()

	#print str(percentiles)

	## Data Distribution testing/Training
	data = FileReader(data_file)
	data = Instances(data)
	data = Instances(data , 0 , n  - (n % folds) )
	n = n- (n % folds)
	print data.numInstances()
	len_fold = int(math.floor(n/folds))
	folds_test = []
	folds_train = []
	for i in range(0,n+1,len_fold)[:-1]:
		folds_test.append(Instances(data,i,len_fold))
		f = open(''.join([directory , ''.join(['fold_test_' , str(i/len_fold) , '.arff'])]) , "w")
		f.write(str(folds_test[-1]));
		f.close()
		temp = Instances(data, 0 , n)
		for j in range(i,i+len_fold,1):
			temp.delete(i)
		folds_train.append(temp)	
		f = open(''.join([directory , ''.join(['fold_train_' , str(i/len_fold) , '.arff'])]) , "w")
		f.write(str(folds_train[-1]));
		f.close()


	## Prediction
	buffers = [] ## List of per fold predictions
	weights = [] ## List of per fold weights per attribute


	for fld in range(0,folds):
		train =  folds_train[fld]
		test =  folds_test[fld]
		train.setClassIndex(data.numAttributes() - 1)
		test.setClassIndex(data.numAttributes() - 1)
		lr = LR()
		lr.buildClassifier(train)
		buf= StringBuffer()  # buffer for the predictions
		attRange = Range()  # no additional attributes output
		outputDistribution = Boolean(False)
		evaluation = Evaluation(test)
		evaluation.evaluateModel(lr, test, [buf, attRange, outputDistribution])
		buffers.append(buf)
		## Writing Evaluation Summaries
		f = open(''.join([directory , ''.join(['summary_',str(fld),'.report'])]) , 'w')
		f.write(evaluation.toSummaryString(True))
		f.close()

		f = open(''.join([directory , ''.join(['coeff_',str(fld),'.report'])]) , 'w')
		f.write(str(lr))
		f.close()

	## Writing predictions in a file

	f = open(''.join([directory , 'prediction.weka']) , 'w')
	for prediction in buffers:
		f.write(str(prediction))
	f.close()


	## Finding top n features

	feat_file = open(''.join([directory , ''.join(['top_' , str(top_n) , '_features.txt'])]) , 'w')
	coefficients = []
	for i in range(0,folds):
		coeff_fold = []
		with open(''.join([directory , ''.join(['coeff_',str(i),'.report'])])) as f:
			for line in f:
				if line.endswith('+\n'):
					term = line.split('*',1)
					coeff_fold.append((term[1][:-2] , abs(float(term[0]))))
		coeff_fold = sorted(coeff_fold , key=lambda tup: tup[1] , reverse=True)
		coefficients.append(coeff_fold)
		feat_file.write(''.join(['Fold No.: ' ,str(i) ,'\n' ]))
		for j in range(0,top_n):
			if len(coeff_fold)>j:
				feat_file.write(''.join(['	' , str(coeff_fold[j][0]) , ' : ',  str(coeff_fold[j][1]) ]))
				feat_file.write('\n')
	feat_file.close()



	## Accuracy Measurement 
	predict_real = []   ## [[pre1,real1],[pre2,re2]]
	with open(''.join([directory , 'prediction.weka'])) as f:
		for line in f:
			if line.startswith(',') or line.startswith('[') or line.startswith(']'):
				line[1:]
			vals = line.split()
			predict_real.append([float(vals[2]) , float(vals[1])])




	predict_real = sorted(predict_real , key=lambda tup: tup[1])
	half_step  = step_size/2
	range_step = n*half_step/100




	outofbin = 0
	inf = float('inf')
	percentiles = [float('-inf')]+(map(float , percentiles))[1:-1]+[inf]
	bins = len(percentiles)
	num_instance = len(predict_real)

	## Variable bins per real value

	# for i in range(0, num_instance):
	# 	prd = predict_real[i][0]
	# 	rl = predict_real[i][1]
	# 	upper_index = i + range_step
	# 	lower_index = i - range_step

	# 	if lower_index < 0:
	# 		limit_min = float('-inf')
	# 	else:
	# 		limit_min  = predict_real[lower_index][1]

	# 	if upper_index > num_instance-1:
	# 		limit_max = float('inf')
	# 	else:
	# 		limit_max = predict_real[upper_index][1]

	# 	if prd < limit_min or prd >limit_max:
	# 		outofbin = outofbin+1
	# 	print ''.join([str(rl) , ' , ' , str(prd) , ' , ' , str(limit_min) , '-', str(limit_max)])




	## Either variable  or fixed bins Both acceptable

	for i in range(0, num_instance):
		prd = predict_real[i][0]
		rl = predict_real[i][1]
		upper_index = i + range_step
		lower_index = i - range_step
		inbin=False
		if lower_index < 0:
			limit_min = float('-inf')
		else:
			limit_min  = predict_real[lower_index][1]

		if upper_index > num_instance-1:
			limit_max = float('inf')
		else:
			limit_max = predict_real[upper_index][1]

		if prd >=limit_min and prd <limit_max:
			inbin = True

		for j in range(0,bins-1):
			if rl >= percentiles[j] and rl<percentiles[j+1]:
				if prd >=percentiles[j] and prd<percentiles[j+1]:
					inbin=True

		if not inbin:
			outofbin = outofbin+1


	#	print ''.join([str(rl) , ' , ' , str(prd) , ' , ' , str(limit_min) , '-', str(limit_max)])


	## Percentile based fixed bins

	# for i in range(0,num_instance):
	# 	prd = predict_real[i][0]
	# 	rl = predict_real[i][1]
	# 	for j in range(0,bins-1):
	# 		if rl >= percentiles[j] and rl<percentiles[j+1]:
	# 			if prd >=percentiles[j] and prd<percentiles[j+1]:
	# 				break
	# 			else:
	# 				outofbin=outofbin+1
	# 				break


	accuracy = float((float(num_instance - outofbin ) * float(100) ) / float(num_instance))
	print ''.join(['Total Instances : ' , str(num_instance)])
	print  ''.join(['Different bins: ' , str(outofbin)])
	print ''.join(['Accuracy: ' , str(accuracy)])









