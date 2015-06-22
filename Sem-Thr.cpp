/*
 * Sean Vincent
 * CS 4348.501
 * Project 2 - Threads and Semaphores
 * 11/8/14
 */

// List of included libraries
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <iostream>
#include <cstdlib>
#include <queue>
#include <ctime>

using namespace std;

// Array size constants
const int transNum = 15;
const int threadNum = 5;
const int threadTel = 2;
const int threadOff = 1;

// Random numbers
int task[threadNum] = {0, 0, 0, 0, 0};
int amount[threadNum] = {0, 0, 0, 0, 0};

// Counters
int employ = 0;
int trans[threadNum] = {0, 0, 0, 0, 0};

// Balance arrays and customer queue
int custBal[threadNum] = {1000, 1000, 1000, 1000, 1000};
int custBor[threadNum] = {0, 0, 0, 0, 0};
queue<int> custQue;

// Declaring the bank employee threads
pthread_t telThr[threadTel];
pthread_t offThr[threadOff];

// List of declared Semaphores
sem_t tellerLane, officerLane;
sem_t finished[transNum];
sem_t custReadyT, custDoneT;
sem_t custReadyO, custDoneO;
sem_t requestT, processT;
sem_t requestO, processO;
sem_t mutex1, mutex2, mutex3, mutex4, mutex5;

// Function prototypes
void *customer(void *);
void *bankTeller(void *);
void *loanOfficer(void *);

int main()
{
    int semCount;
    int thrCount;
    int endCount;
    int sumCount;
    int status;
    int totalBal = 0;
    int totalBor = 0;
    
    // Declaring the array of customer threads
    pthread_t custThr[threadNum];
    
    // Initializing the Semaphores
    if(sem_init(&tellerLane, 0, 2) == -1 || 
            sem_init(&officerLane, 0, 1) == -1 || 
            sem_init(&custReadyT, 0, 0) == -1 || 
            sem_init(&custDoneT, 0, 0) == -1 || 
            sem_init(&custReadyO, 0, 0) == -1 || 
            sem_init(&custDoneO, 0, 0) == -1 || 
            sem_init(&requestT, 0, 0) == -1 || 
            sem_init(&processT, 0, 0) == -1 || 
            sem_init(&requestO, 0, 0) == -1 || 
            sem_init(&processO, 0, 0) == -1 || 
            sem_init(&mutex1, 0, 0) == -1 || 
            sem_init(&mutex2, 0, 0) == -1 || 
            sem_init(&mutex3, 0, 0) == -1 || 
            sem_init(&mutex4, 0, 1) == -1 || 
            sem_init(&mutex5, 0, 1) == -1)
    {
        cout << "Semaphore Initialization Failed" << endl;
        exit(0);
    }
    for(semCount = 0; semCount < transNum; semCount++)
    {
        if(sem_init(&finished[semCount], 0, 0) == -1)
        {
            cout << "Semaphore Initialization Failed" << endl;
            exit(0);
        }
    }
    
    // Creating the customer Threads and 
    // releasing them to the semaphore
    for(thrCount = 0; thrCount < threadNum; thrCount++)
    {
        int *pnum = (int*)malloc(sizeof(int));
        *pnum = thrCount;
        
        status = pthread_create(&custThr[thrCount], NULL, 
                customer, (void *)pnum);
        
        if(status != 0)
        {
            cout << "Thread Creation Failed" << endl;
            exit(0);
        }
        
        sem_wait(&mutex5);
        cout << "Customer " << thrCount << " created." << endl;
        sem_post(&mutex5);
        
        // Signaling to start
        sem_post(&mutex1);
    }
    
    // Joining the customer Threads after they have completed
    for(endCount = 0; endCount < threadNum; endCount++)
    {
        status = pthread_join(custThr[endCount], NULL);
        
        if (status != 0)
        {
           cout << "Thread Joining Failed" << endl;
           exit(0);
        }
        
        cout << "Customer " << endCount 
                << " is joined by main." << endl;
    }
    
    // Printing the summary report
    cout << endl << "\t\tBank Simulation Summary" << endl;
    cout << endl << "\t\tEnding Balance" 
            << "\tLoan Amount" << endl << endl;
    
    // Displaying customer balances
    for(sumCount = 0; sumCount < threadNum; sumCount++)
    {
        cout << "Customer " << sumCount << "\t" 
                << custBal[sumCount] << "\t\t" 
                << custBor[sumCount] << endl;
        
        totalBal += custBal[sumCount];
        totalBor += custBor[sumCount];
    }
    
    // Displaying balance totals
    cout << endl << "Totals" << "\t\t" << totalBal 
            << "\t\t" << totalBor << endl;
    
    return 0;
}

void *customer(void *arg)
{
    int empThr;
    int status;
    
    int *pnum = (int *)arg;
    int custNum = *pnum;
    free(arg);
    
    // Waiting to start
    sem_wait(&mutex1);
    
    // Creates the employee threads the first time through
    if(employ == 0)
    {
        employ++;
        
        // Creating the bank teller Threads and 
        // releasing them to the semaphore
        for(empThr = 0; empThr < threadTel; empThr++)
        {
            int *pnum = (int*)malloc(sizeof(int));
            *pnum = empThr;
            
            status = pthread_create(&telThr[empThr], NULL, 
                    bankTeller, (void *)pnum);

            if(status != 0)
            {
                cout << "Thread Creation Failed" << endl;
                exit(0);
            }

            sem_wait(&mutex5);
            cout << "Bank Teller " << empThr 
                    << " created." << endl;
            sem_post(&mutex5);
            
            // Signaling to start
            sem_post(&mutex2);
        }

        // Creating the loan officer Threads and 
        // releasing them to the semaphore
        for(empThr = 0; empThr < threadOff; empThr++)
        {
            int *pnum = (int*)malloc(sizeof(int));
            *pnum = empThr;
            
            status = pthread_create(&offThr[empThr], NULL, 
                    loanOfficer, (void *)pnum);

            if(status != 0)
            {
                cout << "Thread Creation Failed" << endl;
                exit(0);
            }

            sem_wait(&mutex5);
            cout << "Loan Officer " << empThr 
                    << " created." << endl;
            sem_post(&mutex5);
            
            // Signaling to start
            sem_post(&mutex3);
        }
    }
    
    sem_wait(&mutex5);
    cout << "Customer " << custNum 
            << " has entered the bank." << endl;
    sem_post(&mutex5);
    
    // Each customer will make three separate transactions
    while(trans[custNum] < 3)
    {
        // Seeding the random generator
        srand(time(NULL) + custNum);
        
        // Getting the random task and dollar amount
        task[custNum] = rand() % 3;
        amount[custNum] = ((rand() % 5) + 1) * 100;
        
        // Deposit = 0
        // Withdrawal = 1
        // Loan = 2
        
        // Choosing based on task number
        if(task[custNum] == 0 || task[custNum] == 1)
        {
            // Teller Lane is occupied
            sem_wait(&tellerLane);
            
            // Pushing onto the queue and signaling ready
            // one customer at a time
            sem_wait(&mutex4);
            custQue.push(custNum);
            sem_post(&custReadyT);
            sem_post(&mutex4);
            
            sem_wait(&mutex5);
            cout << "Customer " << custNum 
                << " approaches the front desk." << endl;
            sem_post(&mutex5);
            
            sleep(1);
        
            // Customer requests the task from the bank teller
            sem_post(&requestT);

            // Customer waits for the bank teller to process task
            sem_wait(&processT);

            // Customer waits for the bank teller 
            // to signal finished
            sem_wait(&finished[custNum]);
            
            sleep(1);

            // Customer signals done to bank teller
            sem_post(&custDoneT);
            
            sem_wait(&mutex5);
            cout << "Customer " << custNum 
                    << " leaves the front desk." << endl;
            sem_post(&mutex5);
        }
        else if(task[custNum] == 2)
        {
            // Officer Lane is occupied
            sem_wait(&officerLane);
            
            // Pushing onto the queue and signaling ready
            // one customer at a time
            sem_wait(&mutex4);
            custQue.push(custNum);
            sem_post(&custReadyO);
            sem_post(&mutex4);
            
            sem_wait(&mutex5);
            cout << "Customer " << custNum 
                << " approaches the front desk." << endl;
            sem_post(&mutex5);
            
            sleep(1);
        
            // Customer requests the task from the loan officer
            sem_post(&requestO);
            
            // Customer waits for the loan officer to process task
            sem_wait(&processO);

            // Customer waits for the loan officer 
            // to signal finished
            sem_wait(&finished[custNum]);
            
            sleep(1);

            // Customer signals done to loan officer
            sem_post(&custDoneO);
            
            sem_wait(&mutex5);
            cout << "Customer " << custNum 
                    << " leaves the front desk." << endl;
            sem_post(&mutex5);
        }
        
        trans[custNum]++;
    }
    
    sem_wait(&mutex5);
    cout << "Customer " << custNum 
            << " has exited the bank." << endl;
    sem_post(&mutex5);
}

void *bankTeller(void *arg)
{
    int t_cust;
    
    int *pnum = (int *)arg;
    int telNum = *pnum;
    free(arg);
    
    // Waiting to start
    sem_wait(&mutex2);
    
    // Continuously serve the customer
    while(true)
    {
        // Waiting on customer to signal ready
        sem_wait(&custReadyT);
        
        // Popping the customer number out of the queue
        sem_wait(&mutex4);
        t_cust = custQue.front();
        custQue.pop();
        sem_post(&mutex4);
        
        sem_wait(&mutex5);
        cout << "Bank Teller " << telNum 
                << " begins serving customer " << t_cust << endl;
        sem_post(&mutex5);
        
        // Waiting on customer to request a task
        sem_wait(&requestT);
        
        // Bank Teller processes customer request
        if(task[t_cust] == 0) // Deposit
        {
            sem_wait(&mutex5);
            cout << "Customer " << t_cust 
                    << " requests Bank Teller " << telNum 
                    << " to make a deposit of $" << amount[t_cust] 
                    << endl;
            sem_post(&mutex5);
            
            sleep(4);
            
            custBal[t_cust] = custBal[t_cust] + amount[t_cust];
            
            sem_wait(&mutex5);
            cout << "Bank Teller " << telNum 
                    << " processes deposit of $" << amount[t_cust] 
                    << " for customer " << t_cust << endl;
            sem_post(&mutex5);
        }
        else if(task[t_cust] == 1) // Withdrawal
        {
            sem_wait(&mutex5);
            cout << "Customer " << t_cust 
                    << " requests Bank Teller " << telNum 
                    << " to make a withdrawal of $" 
                    << amount[t_cust] << endl;
            sem_post(&mutex5);
            
            // Check too see if their balance is high enough
            if(amount[t_cust] > custBal[t_cust])
            {
                sem_wait(&mutex5);
                cout << "Customer " << t_cust 
                        << " has insufficient funds" 
                        << " in their account." << endl;
                sem_post(&mutex5);
            }
            else
            {
                sleep(4);
                
                custBal[t_cust] = custBal[t_cust] - amount[t_cust];
                
                sem_wait(&mutex5);
                cout << "Bank Teller " << telNum 
                        << " processes withdrawal of $" 
                        << amount[t_cust] << " for customer " 
                        << t_cust << endl;
                sem_post(&mutex5);
            }
        }
        
        // Signaling customer that request has been processed
        sem_post(&processT);
        
        // Signaling customer that task has been completed
        sem_post(&finished[t_cust]);
        
        if(task[t_cust] == 0) // Deposit
        {
            sem_wait(&mutex5);
            cout << "Customer " << t_cust 
                    << " gets receipt from Bank Teller " 
                    << telNum << endl;
            sem_post(&mutex5);
        }
        else if(task[t_cust] == 1) // Withdrawal
        {
            sem_wait(&mutex5);
            cout << "Customer " << t_cust 
                    << " gets cash and receipt from Bank Teller " 
                    << telNum << endl;
            sem_post(&mutex5);
        }
        
        // Waiting for customer to signal done
        sem_wait(&custDoneT);
        
        // Teller Lane is open
        sem_post(&tellerLane);
    }
}

void *loanOfficer(void *arg)
{
    int o_cust;
    
    int *pnum = (int *)arg;
    int offNum = *pnum;
    free(arg);
    
    // Waiting to start
    sem_wait(&mutex3);
    
    // Continuously serve the customer
    while(true)
    {
        // Waiting on customer to signal ready
        sem_wait(&custReadyO);
        
        // Popping the customer number out of the queue
        sem_wait(&mutex4);
        o_cust = custQue.front();
        custQue.pop();
        sem_post(&mutex4);
        
        sem_wait(&mutex5);
        cout << "Loan Officer " << offNum 
                << " begins serving customer " << o_cust << endl;
        sem_post(&mutex5);
        
        // Waiting on customer to request a task
        sem_wait(&requestO);
        
        sem_wait(&mutex5);
        cout << "Customer " << o_cust 
                << " requests Loan Officer " << offNum 
                << " to apply for a loan of $" << amount[o_cust] 
                << endl;
        sem_post(&mutex5);
        
        sleep(4);
        
        // Loan Officer processes customer request
        // Loan
        custBor[o_cust] = custBor[o_cust] + amount[o_cust];
        
        sem_wait(&mutex5);
        cout << "Loan Officer " << offNum 
                << " approves loan of $" << amount[o_cust] 
                << " for customer " << o_cust << endl;
        sem_post(&mutex5);
        
        // Signaling customer that request has been processed
        sem_post(&processO);
        
        // Signaling customer that task has been completed
        sem_post(&finished[o_cust]);
        
        sem_wait(&mutex5);
        cout << "Customer " << o_cust 
                << " gets loan from Loan Officer " 
                << offNum << endl;
        sem_post(&mutex5);
        
        // Waiting for customer to signal done
        sem_wait(&custDoneO);
        
        // Officer Lane is open
        sem_post(&officerLane);
    }
}
