### Context and Roles
**Name**: Mark
  * Works as an operations manager that logs in regularly to monitor flight statuses and generate reports.
  * He works on a company-issued laptop and regularly logs into the website as a member of staff.

### Goals and Success
**Primary goal**: Trying to gain visibility into all reservations, cancellations and flight information to help him with his weekly reports.
  * He will be done with his goal when he is able to successfully generate a report that includes the number of bookings per flight, the most popular routes and peak booking times.

### Pain Points
* Must not take hours to generate a simple report – without a proper system he would have to manually check through a large amount of data.
* Data must be properly stored and easily accessible – Should not be scattered throughout the system.
* Will abandon the system if large data queries freeze or crash the system.

### Constraints and Non-Goals
#### Constraints
* Limited time (As a busy staff member) – System must be highly responsive and generate data quickly

#### Non-Goals
* Doesn’t care about visual appeal or any customer marketing features in the system
* Doesn’t want to learn about query languages to access data – Needs intuitive tools to generate reports and access data.

---

## User Stories

### 1. Flight and Reservation Tracking Dashboard
> **As a** member of staff,
> I can view a dashboard tracking all flight availability,
> cancellations and individual reservations, so that I can quickly identify any operational issues and monitor real time capacity without checking multiple systems.

**Acceptance Criteria:**
* **Criterion 1**: Given that I am on the dashboard page, when I press the flight availability option, then I should see all the registered flight information.
* **Criterion 2**: Given that a specific flight is cancelled, when I am on the dashboards’ flight availability page, then I should see a clear indication that the specific flight has been cancelled in the dashboard.
* **Criterion 3**: Given that I am on the dashboard page, when I input a specific booking reference number, then the system should display the associated booking information such as flight details, passenger information and ticket status.

### 2. Generating Reports
> **As an** operations manager,
> I can generate automated reports detailing bookings per flight,
> the most popular routes and peak booking times,
> so that I can provide accurate data to the administrators quickly and efficiently.

**Acceptance Criteria:**
* **Criterion 1**: Given that I am trying to access specific data (e.g. peak booking times), when I navigate to the appropriate page, then the system should display the data I need as a clear visual breakdown.
* **Criterion 2**: Given that I am trying to generate a report, when I select the option to generate an automated report, then a proper report containing clear information should be generated.