#include <deque>
#include <map>
#include <algorithm>
#include <ucontext.h>
#include <iostream>
using namespace std;


int stackSize=8192;

struct tcb {
    int id;
    ucontext_t context;
    deque<tcb*> waiting;
    char *stack;
    
    tcb(int i);
};
tcb::tcb(int i) {
    id = i;
    stack = new char[stackSize];
}

