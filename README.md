# SharedWallet

**SharedWallet** is an Android application designed to help users track and manage debts within groups. A practical example would be a trip with friends, where a group is created, and all friends are added. Within this group, all the expenses of the trip can be entered, and the total debts are calculated and displayed. The app ensures transparency in shared costs, making it easier to manage finances between friends, roommates, or colleagues.

### Features

#### 1. Multi-User Management
SharedWallet supports multiple users, allowing anyone to join groups and add their expenses. Users can add friends via username or email, and these friends can be included in any debt-sharing group. Once added to a group, all members can manage expenses and track the collective debt.

#### 2. Firebase Backend
The application uses **Firebase** for its backend. Firebase Authentication allows for easy user sign-in, while Firestore is used to store user data, group data, and expenses. This setup ensures secure, real-time data synchronization across all users' devices.

#### 3. Main Screens
There are three main screens within the app:
![Screenshot_20250115_135634.png](../../Desktop/Screenshot_20250115_135634.png)

**Groups:** The core feature of the app, where users can create, view, and manage debt-sharing groups. Users can add friends to groups and create expenses. The total debt is calculated automatically based on group participation.
![Screenshot_20250115_140200.png](../../Desktop/Screenshot_20250115_140200.png)
**Activity:** A log of all expenses across all groups the user is part of, making it easy to see what has been spent lately.
![Screenshot_20250115_140927.png](../../Desktop/Screenshot_20250115_140927.png)
**Friends:** Users can search for and add friends using either their username or email. This screen is essential for managing the people you are sharing expenses with.
![Screenshot_20250115_140022.png](../../Desktop/Screenshot_20250115_140022.png)

#### 4. Push Notifications
Push notifications are implemented to keep users informed. Every 30 seconds, the app checks if a user has been added to a new group. If so, a push notification is sent with the group name, ensuring users are always aware of new group memberships.
![Screenshot_20250115_140635.png](../../Desktop/Screenshot_20250115_140635.png)

#### 5. Debt Management
The main functionality of the app is to split debts within a group. After creating a group and inviting friends, users can:
![Screenshot_20250115_140504.png](../../Desktop/Screenshot_20250115_140504.png)

**Add friends:** Once friends are added via the "Friends" screen, they can be added to any group.
**Create expenses:** Expenses can be added with details like description, location, and amount. The amount can be split equally among all members of the group or individually. Each expense is listed with the amount, and the total debt for each member is updated accordingly.
**Settle debts:** The app allows users to pay off all outstanding debts at once, simplifying the process of clearing balances.
**Calculate total debt:** Each user can see how much they owe or are owed within the group, providing a clear overview of the financial situation.
**Leave a group:** A user can leave a group, but only after all debts have been settled, ensuring no outstanding balances remain.

#### 6. User Interface and Experience
The app's design focuses on simplicity and ease of use. The interface is intuitive, ensuring that users can quickly create groups, add expenses, and calculate debts. Push notifications provide real-time updates, keeping users in the loop.

### Conclusion

SharedWallet makes managing shared expenses straightforward. Whether it's a group trip, a roommate situation, or any scenario where multiple people share costs, SharedWallet ensures that debts are divided fairly, transparently, and efficiently. With Firebase-powered backend and push notifications, the app ensures a seamless experience across all users.