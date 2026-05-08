### Context and Roles
**Name**: John
  * A business consultant who likes to travel frequently.
  * He doesn’t mind spending more money to have a better flight experience.

### Goals and Success
**Primary goal**: Quickly find and book flights for work and holiday trips.

### Pain Points
* Must need a simple booking system to navigate through the system easily.
* Cancellation system must be straightforward and quick.


### Constraints and Non-Goals
#### Constraints
* Mostly uses mobile devices while travelling.
* Has limited time and often books flights quickly between meetings
* Very strict scheduling dates (Needs to get back before the holidays end, so cannot shift the planned flight dates by much)

#### Non-Goals
* Not interested in detailed airline management features, only booking and modifying flights
* Not interested in signing up for promotional emails

---

## User Stories

### 1. Manage Bookings
> **As a** frequent customer,
> I can view my bookings,
> so that I can keep track of my travel plans.

### 2. Confirmation Email
> **As a** customer, 
> I will receive a booking confirmation, 
> so that I will get the ticket information quickly and know my booking was successful.

**Acceptance Criteria:**
* **Criterion 1**: Given that I have booked a flight, when I receive an email about the booking, then I should be able to clearly see if the booking is successful and the ticket information (if successful).
* **Criterion 2**: Given that I have booked a flight, when the flight is delayed or cancelled, then I should receive an email about the disturbance.

### 3. Loyalty Point System
> **As a** frequent customer, 
> I can gain loyalty points when having an account, 
> so that I can get better discounts on later purchases.

**Acceptance Criteria:**
* **Criterion 1**: Given that I have a registered account, when I book a flight, then I will gain loyalty points that should be displayed in my profile.
* **Criterion 2**: Given that I have enough loyalty points, when I am trying to purchase a new flight, then there should be an option to apply the points I have collected as a discount.