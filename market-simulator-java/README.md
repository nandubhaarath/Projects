# Market Simulator (Java)

## Overview

This project is a small electronic market simulator written in Java.  
It explores how order matching systems handle fairness, latency, concurrency, and load.

The implementation focuses on systems behaviour rather than financial modelling, using simple structures to make design decisions and trade-offs clear.



## What the Project Does

The simulator models the core components of an exchange:

- An order book with price–time priority  
- A matching engine supporting limit and market orders  
- A latency model using discrete-event simulation  
- A concurrent ingestion pipeline with backpressure  
- Metrics for throughput and queue-wait latency  



## Order Matching and Fairness

Orders are matched using standard price–time priority rules:

- Best price is matched first  
- Orders at the same price are filled FIFO  

Sorted price levels and FIFO queues ensure predictable and fair execution.



## Latency and Arrival Timing

A discrete-event simulator schedules order arrivals with timestamps.  
This demonstrates how execution order depends on arrival time rather than when an order was created.



## Concurrency Design

Orders are submitted concurrently by multiple producer threads.  
A single consumer thread owns the order book and processes orders sequentially.

This design preserves correctness under concurrency and keeps the matching logic deterministic.

A separate demo illustrates concurrent behaviour when access is not coordinated.



## Backpressure and Load Handling

The system uses bounded queues to manage load:

- Orders may block or fail fast when the queue is full  
- Queue wait times are recorded to observe latency under pressure  



## Metrics and Analysis

The simulator records:

- Orders processed per second  
- Average and maximum queue wait  
- p50, p95, and p99 queue-wait latency  

A stress sweep runs the system with different queue sizes to observe how buffering affects latency and rejection rates.



## Project Structure
Order / Trade / Side / OrderType Domain model
OrderBook Price–time order book
MatchingEngine Matching logic
Simulator Discrete-event latency model

ConcurrentExchange Concurrent ingestion pipeline
NaiveConcurrentDemo Concurrency behaviour example
StressSweep Queue-capacity stress test
Main Example runner


## How to Run

Compile: 
mvn compile


Run examples:
- `Main` – basic matching behaviour  
- `NaiveConcurrentDemo` – concurrent behaviour demonstration  
- `StressSweep` – latency and queue-capacity analysis  



## Key Observations

- A single-writer design keeps shared state consistent under load  
- Queue size affects tail latency more than throughput  
- p95 and p99 latency reveal behaviour not visible in averages  


## Scope

This is a simulation intended for learning and experimentation.  
It does not include networking, persistence, or market data feeds.



## Author

Nandeeshvaran Bhaarath  
BSc Computer Science (Year in Industry)  
University of Birmingham
