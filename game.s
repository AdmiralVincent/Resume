# Pre-defined constants
.eqv MAX_LETTERS 12           			# max words length
.eqv MILLS_IN_SECOND 1000   			# 1 second contains 1000 milliseconds
.eqv ALLOWED_TIME 180000 			# 3 minutes allowed
# =============================================================================
# This macros prints integer values to console
# %number parameter can be a number or register
.macro printInteger(%number)
	li  $v0, 1				# place "print integer" syscall code to $v0
	add $a0, $zero, %number 		# place %number to $a0 register
	syscall					# execute command
.end_macro

# =============================================================================
# This macros prints single character to console
# %char parameter can be a register or ASCII value
.macro printChar(%char)
	add $a0, $zero, %char 			# place %char to $a0 register
	li	$v0, 11           		# place "print character" syscall code to $v0
	syscall               			# execute command
.end_macro

# =============================================================================
# This macros prints string to the console. %string parameter is a
# label of the string in the memory
.macro printString(%string)
	li $v0, 4	      			# place "print string" syscall code to $v0
	la $a0, %string   			# load address of the string to $a0
	syscall           			# execute command
.end_macro

# =============================================================================
# This macros prints prompt to the console. %string parameter is a
# string literal
.macro prompt (%string)

# place string to the memory with "myString" as label ==============
	.data
	myString: .asciiz %string 
	.text
# =============================================================================
	li $v0, 4        # place "print string" syscall code to $v0
	la $a0, myString # load address of the string to $a0
	syscall          # execute command
.end_macro

# =============================================================================
# This macros sets seed for random numbers generator
# %startValue can be a register or a number
.macro setSeed(%startValue)
	li $a0, 1				# set pseudorandom number generator (any int).
	add $a1, $zero, %startValue  		# place seed value to %a0
	li $v0, 40                   		# place "set seed" syscall code to $v0
	syscall					# execute command
.end_macro

# =============================================================================
# This macros gets current time in milliseconds
# Result will be retured in %regLowPart and %regHighPart
# This parameters must be a registers
.macro getCurrTimeTo(%regLowPart, %regHighPart)
	li $v0, 30
	syscall
	add %regLowPart, $zero, $a0
	add %regHighPart, $zero, $a1
.end_macro

# =============================================================================
# This macro generates random character from in range 'A'..'Z'
# ASCII code of character will be returned in $a0
.macro getRandomCharacter
	li $v0, 42      			# place "random int range" syscall code to $v0
	li $a0, 5       			# i.d. of pseudorandom number generator (any int).
	li $a1, 26      			# place upper bound of range of returned values to $a1
	syscall         			# execute command 
	add $a0, $a0, 65 # add 65 ('A' ASCII code) to returned value
.end_macro

# =============================================================================
# This macro generates %count of random characters and place them
# to destination
.macro generateCharacters (%destination, %count)
	getCurrTimeTo($t0, $t1)  		# get current time
	setSeed($t0)             		# set seed for random generator

	la $a2, %destination     		# load address of %destination to $a2
	li $t0, 0                # set %t0 as 0
	Loop:                    
		getRandomCharacter() 		# get random character
		sb $a0, ($a2)        		# save character to destination address
		add $a2, $a2, 1      		# increase destination address by 1
		add $t0, $t0, 1      		# increase $t0 by 1
	blt $t0, %count, Loop    		# if $t0 > %count then jump to Loop
                             			# else go to next instruction
.end_macro

# =============================================================================
# This macro displays 3x3 grid of characters
# %randomCharacters is string in memory where characters are saved
.macro displayGrid (%randomCharacters)
	la	$a3, %randomCharacters 		# load address of %randomCharacters to $a3
	li	$t0, 0                	 	# set $t0 as 0
	Loop:                      		# loop label
		lb	$a0, ($a3)         	# load byte from %randomCharacters($a3) to $a0
		beq $a0, 0, Exit       		# if $a0 == 0 then exit from loop
		printChar($a0)         		# print character to console
		li	 $a0, ' '          	# place space to $a0
		printChar($a0)         		# print character to console
		addi $t0, $t0, 1       		# increase $t0 by 1
		bne  $t0, 3, Next      		# if $t0 != 3 then jump to Next label
		li	 $t0, 0            	# else set $t0 to 0
		li	 $a0, '\n'         	# place "new line" character to $a0
		printChar($a0)         		# print character to console
	Next:
		addi $a3, $a3, 1       		# increase address in $a3
	j	Loop                   		# jump to Loop label without any conditions
	Exit:
.end_macro

# =============================================================================
# This macros reads string from the user
# %buffer: string label in memory where to save input
# %maxLength: max length of characters to read
.macro readString(%buffer, %maxLength)
	la	$a0, %buffer            	# load address of %buffer to $a0 
	add $a1, $zero, %maxLength  		# place %maxLengt to $a1
	li	$v0, 8                  	# place "read string" syscall code to $v0
	syscall                     		# execute command
	la  $a0, %buffer			# load address of %buffer to $a0 
	Loop:					# Read loop
		lb  $a1, ($a0)          	# get character
		beq $a1, '\n', Exit     	# if character == "new line" then jump to Exit
		addi $a0, $a0, 1        	# increase address to get next character
	j Loop					# Read next character
Exit:
	sb  $zero, ($a0)            		# replace "new line" with 0 
.end_macro

# =============================================================================
# This macros checks if character exists in the word
# Return value: $a0 = 1 if exists and 0 if not
.macro charInWord(%char, %word)
	add $t0, $zero, %char     		# place character to $t0
	la	$a0, %word            		# load address of %word to $a0 
	Loop:                     		# Read loop
		lb   $a1, ($a0)       		# get character into $a1
		beq  $a1, 0, NotIn    		# if $a1 == 0(end of string) then jump to NotIn
		beq  $a1, $t0, In     		# if $a1 == %char then jump to In
		addi $a0, $a0, 1     		# increase address to read next character
	j Loop                    		# Read next
In:
	li	$a0, 1				# set $a0 = 1
	j Exit                    		# jump to exit
NotIn:
	li	$a0, 0                		# set $a0 = 0
Exit:
.end_macro

# =============================================================================
# This macro checks if %word consists only from %character
# return value: $a0 = 1 if consists and 0 if not
.macro wordConsistsFromCharacters(%word, %characters)
	la	$a2, %word			# load address of %word to $a2 
	Loop:					# Read loop
		lb	$t5, ($a2)		# get character into $a1 from %word
		beq $t5, 0, Yes                 # if character == 0 then jump to Yes
		charInWord($t5, %characters)    # check if character in allowed list
		beq $a0, $zero, No              # if $a0 == 0 then jump to No
		addi $a2, $a2, 1                # increase address to read next character
	j Loop					# Read next
Yes:
	li	$a0, 1	# set $a0 = 1
	j	Exit    # jump to exit
No:
	li  $a0, 0  # set $a0 = 0
	Exit:
.end_macro

# =============================================================================
# This macro opens file by name and returns in $v0 file descriptor
.macro openFile(%fileName)
	la	$a0, %fileName    
	li	$a1, 0
	li	$a2, 0
	li	$v0, 13
	syscall
.end_macro

# =============================================================================
# This macro closes file by descriptor
.macro closeFile(%fileDescriptor)
	add	$a0, $zero, %fileDescriptor
	li	$v0, 16
	syscall
.end_macro

# =============================================================================
# This macro reads one line from file %fDescr to %buffer with max length %buffLength
# Returns in $v0 number of characters read (0 if EOF, negative if error)
.macro readLine9(%fDescr, %buffer, %offset)

	add  $t2, $zero, %offset
	Read:
	la	$a1, %buffer           		# load address of %buffer to $a1
	add	$a0, $zero, %fDescr    		# place file descriptor to $a0
	li	 $a2, 11              		# $a2 = number of characters to read
	li	 $v0, 14	           	# place "read from file" syscall code to $v0
	syscall		               		# read from file
	addi $v0, $v0, -2
	add  $a1, $a1, $v0
	sb	 $zero, ($a1)
	addi $t2, $t2, -1
	beq  $t2, $zero, Exit
	j Read
Exit:
	
.end_macro

# =============================================================================
# This macro reads one line from file %fDescr to %buffer with max length %buffLength
# Returns in $v0 number of characters read (0 if EOF, negative if error)
.macro readLine(%fDescr, %buffer)
	la	$a1, %buffer           		# load address of %buffer to $a1
	add	$a0, $zero, %fDescr    		# place file descriptor to $a0
	li	$t0, 0	               		# we will store here number of bytes
	Read:					# Read loop
		li	 $a2, 1	           	# $a2 = number of characters to read
		li	 $v0, 14	        # place "read from file" syscall code to $v0
		syscall		           	# read from file
		beq  $v0, $zero,EndF   
		add  $t0, $t0, $v0     		# accumulate count of readed bytes
		lb	 $t1, ($a1)        	# get last readed character to $t1
		beq	 $t1, '\n', Read   	# if $t1 == new line then read next character
		beq	 $t1, '\r', End    	# if $t1 == carriage return then exit from read loop
		beq  $t1, $zero,End   	 	# if $t1 == 0 then exit from read loop
		addi $a1, $a1, 1       		# increase address to write next byte
	j	Read
End:
	move $v0, $t0              		# $v0 = $t0
	addi $v0, $v0, -1          		# $v0 = $v0 - 1
	j	Exit
EndF:
	li  $v0, 0                 		# #v0 = 0
Exit:
	sb	$zero, ($a1)           		# replace '\r' with 0

.end_macro

# =============================================================================
# This macro compares two strings
# Return value: %v0 = 0 is strings are equals and -1 if not
.macro strcmp(%szStr1, %szStr2)
	la	$a0, %szStr1          		# load address of %szStr1 to $a0 
	la	$a1, %szStr2		  	# load address of %szStr2 to $a1
	Read:					# Read loop
		lb	 $t0, ($a0)       	# get character from %szStr1
		lb	 $t1, ($a1)	      	# get character from %szStr2
		bne	 $t0, $t1, NotEq  	# if they are not equals then jump to NotEq
		beq	 $t0, $zero, Eq   	# if any character is 0 then jump to Eq
		addi $a0, $a0, 1      		# go to next character in memory for %szStr1
		addi $a1, $a1, 1      		# go to next character in memory for %szStr2
	j Read					# read characters from next position
NotEq:
	li	$v0, -1				# set $v0 as -1
	j	Exit                  		# jump to exit
Eq:
	li	$v0, 0                		# set $v0 as 0
Exit:
.end_macro

# =============================================================================
# This macro
.macro searchWordInFile(%fDescr, %word)
	Loop:
		add $t0, $zero, %fDescr
		readLine($t0, szWordFromFile)
		beq $v0, $zero, Exit
		# UNCOMMENT NEXT LINES FOR DEBUG
		# printString(szWordFromFile)
		# printChar('\n')
		# ==============================
		strcmp(szWordFromFile, %word)
		bne $v0, $zero, Loop
		li	$v0, 1
	Exit:
.end_macro

# =============================================================================
# This macro
.macro possibleWords(%fDescr, %characters)
	Loop:
		add $t0, $zero, %fDescr
		readLine($t0, szWordFromFile)
		beq $v0, $zero, Exit
		wordConsistsFromCharacters(szWordFromFile, %characters)
		bne $a0, 1, Loop
		la   $a0, szRandomCharacters
		addi $a0, $a0, 4
		lb	 $t1, ($a0)   			# $t1 now contains character from the middle of the grid
		charInWord($t1, szWordFromFile)
		bne $a0, 1, Loop
		printString(szWordFromFile)
		printChar('\n')
	j	Loop
	Exit:
.end_macro

# =============================================================================
# Main program data section
.data
szFileName:
	.asciiz "words.txt"

szFileName2:
	.asciiz "9words.txt"

szRandomCharacters:
	.space MAX_LETTERS
	.byte 0
	
dwTimeLowPart:
	.word 0
	
dwTimeHighPart:
	.word 0

dwPoints:	
	.word 0

dwFileDescriptor:
	.word 0
	
szWord:
	.space MAX_LETTERS
	.byte 0

szWordFromFile:
	.space 16
# =============================================================================
# Main program code section
.text
main:
	prompt("\t\tWelcome to my game\n")
	prompt("\nUsing the letters in the grid, you must find as many words as you can in 180 seconds")
	prompt("\nEvery word must contain the letter from the middle of the grid")
	prompt("\nYou can only use each word once")
	prompt("\nYOU MUST LOCK THE CAPS FOR THE GAME TO WORK!!!\n\n")
newGame:
	
	
	getCurrTimeTo($t0, $t1)  		# get current time
	setSeed($t0)             		# set seed for random generator
	
	li $v0, 42      			# place "random int range" syscall code to $v0
	li $a0, 5       			# i.d. of pseudorandom number generator (any int).
	li $a1, 52      			# place upper bound of range of returned values to $a1 (cout of words in 9words.txt)
	syscall           			# execute command 
	
	move $t6, $a0
	
	openFile(szFileName2)
	sw $v0, dwFileDescriptor
		
	move $t7, $v0
	readLine9($t7, szRandomCharacters, $t6)
		
	move $t5, $v0
		
	lw $t0, dwFileDescriptor
	closeFile($t0)

	# generateCharacters(szRandomCharacters, MAX_LETTERS)  # generate random characters
	
	getCurrTimeTo($t0, $t1)                 	# get current time
	sw	$t0, dwTimeLowPart              	# save low part to memory	
	sw  $t1, dwTimeHighPart                 	# save high part to memory
	
	MainLoop:                               	# main program loop
		
		displayGrid(szRandomCharacters)     	# display 3x3 grid of characters
		
		prompt("Word: ")                    	# print prompt
		readString(szWord, MAX_LETTERS)     	# read word from user
		
		getCurrTimeTo($t0, $t1)             	# get current time
		lw	$t2, dwTimeLowPart              # load previous saved low part to $t2 from memory
		sub $t0, $t0, $t2                   	# $t0 = time difference
							# all calculations in milliseconds
		bge $t0, ALLOWED_TIME, ExitMain     	# if passed time > 5 minuts then exit from main loop
		
		div $t0, $t0, MILLS_IN_SECOND       	# $t0 = time difference in seconds
		
		# print seconds past
		printInteger($t0)
		prompt(" seconds past\n")
		
		
		# get char at the middle
		
		la   $a0, szRandomCharacters
		addi $a0, $a0, 4
		lb	 $t0, ($a0)   			# $t0 now contains character from the middle of the grid
		
		# check if middle character is in word
		
		charInWord($t0, szWord)
		beq	 $a0, 1, next1
		prompt("There is no middle character in the word\n\n")
		j MainLoop
		
	next1:
	
		wordConsistsFromCharacters(szWord, szRandomCharacters)
		beq	 $a0, 1, next2
		prompt("Word consists from characters that are not in grid\n\n")
		j MainLoop
		
	next2:
		
		# check word in text file
		openFile(szFileName)
		sw $v0, dwFileDescriptor
		
		move $t5, $v0
		searchWordInFile($t5, szWord)
		
		move $t5, $v0
		
		lw $t0, dwFileDescriptor
		closeFile($t0)
		
		beq	$t5, 1, next3
		
		prompt("Not actual word\n\n")
		
		# Make lose noise
		li $v0, 31
		li $a0, 42
		li $a1, 700
		li $a2, 31
		li $a3, 80
		syscall
		# ===============
		j MainLoop
		
	next3:
	
		prompt("Actual word. You get +1 point\n\n")
		
		# increse and save points
		lw   $t0, dwPoints
		addi $t0, $t0, 1
		sw	 $t0, dwPoints
		# =======================
		
		# Make wim noise
		li $v0, 31
		li $a0, 67
		li $a1, 700
		li $a2, 31
		li $a3, 80
		syscall
		# ===============
		
	j MainLoop
	
ExitMain:
	
	prompt("No more time\n")
	prompt("Your score is: ")
	
	lw   $t0, dwPoints
	printInteger($t0)
	
	prompt("\nDo you want to see all possible words?(1 - Yes, Any other integer - No): ")
	
	li	$v0, 5
	syscall
	
	bne $v0, 1, next4
	
	# list all possible words
	
	openFile(szFileName)
	sw $v0, dwFileDescriptor
		
	move $t7, $v0
	possibleWords($t7, szRandomCharacters)
		
	move $t5, $v0
		
	lw $t0, dwFileDescriptor
	closeFile($t0)
	
next4:

	prompt("\nDo you want to start new game?(1 - Yes, Any other integer - No): ")
	
	li	$v0, 5
	syscall
	
	beq $v0, 1, newGame
	
	prompt("Have a nice day\n")
	
	li $v0, 10
	syscall
	
