#include "tcb.h"

void run_function(int thread_id, void (*func)(int *), int *args);
void mythread_init();
int mythread_fork(void (*func)(int *), int *args);
void mythread_exit();
void mythread_yield();
int mythread_join(int threadid);



map<int, tcb*> thread_list;



int next_id  = 1;

deque<tcb*> queue;

int this_th_th = 1;

void mythread_init() {
   
    tcb *thread = new tcb(next_id);
    next_id++;
    thread_list[thread->id] = thread;

    ucontext_t main;
    getcontext(&thread->context);
    thread->context.uc_stack.ss_sp = thread->stack;
    thread->context.uc_stack.ss_size = stackSize;
    thread->context.uc_link = NULL;
    this_th_th=thread->id;
  
}

void run_function(int thread_id, void (*func)(int *), int *args){
    this_th_th = thread_id;
    func(args);
    mythread_exit();
}



int mythread_fork(void (*function)(int *), int *args) {
    
    // new thread
    tcb *  this_th = thread_list[this_th_th];
    tcb *  thread = new tcb(next_id);
    next_id++;

    thread_list[thread->id] = thread; // Storing forked thread
    
    queue.push_back((tcb *)this_th);
    getcontext(&(thread->context)) ;
    thread->context.uc_stack.ss_sp = thread->stack;
    thread->context.uc_stack.ss_size = stackSize;
    thread->context.uc_link = NULL;
    
    makecontext(&thread->context, (void (*)()) run_function,3, thread->id, function, args);
    swapcontext(&this_th->context, &thread->context) ;
    if (this_th_th != thread->id) {
        return  thread->id;
    } 
        return -1;
}
int mythread_join(int threadid) {
    
    if (thread_list.count(threadid) == 0) {
        return 0; // Invalid ID
    }

    tcb *  this_th = thread_list[this_th_th];
    
    thread_list[threadid]->waiting.push_back((tcb *)this_th); // add join request in the thread's list
      if (queue.empty()) {
        return 0;
    }

    tcb *  thread = queue.front();
    queue.pop_front();
    this_th_th = thread->id;
    swapcontext(&this_th->context, &thread->context); // run other threads while the one requested fininshes
    return 0;
}

void mythread_exit() {

    tcb *this_th = thread_list[this_th_th];
    while (!this_th->waiting.empty()) {
        queue.push_back(this_th->waiting.front());
        this_th->waiting.pop_front();
    }
    deque<tcb*>::iterator it = find(queue.begin(), queue.end(), this_th);
    if (it != queue.end())
        queue.erase(it, it+1);
    if (queue.empty()) {
        exit(0); // no other thread remaining
    }
    // run the next thread if availiable
    tcb *  thread = queue.front();
    queue.pop_front();

    tcb *th = thread_list[this_th_th];
    delete th;
    thread_list.erase(this_th_th);



    this_th_th = thread->id;


    setcontext(&(thread->context));
    

    }

void mythread_yield() {
    
    if (queue.empty()) {
        return;
    }
// swaoing the context
    tcb *  this_th = thread_list[this_th_th];
    tcb *  thread = queue.front();
    queue.pop_front();
        queue.push_back((tcb *)this_th);
    this_th_th = thread->id;
    swapcontext(&this_th->context, &thread->context);

    
}







