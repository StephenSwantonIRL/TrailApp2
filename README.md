## **About Trail App**

Trail app is an Android native app developed in Kotlin which allows users to store details about trails, i.e. collections of places known as trail markers. In version 2 the focus is on moving away from an activity based version of the app to a version that adopts a single activity fragment based approach.

Key features include:
- Create, Read, Update and Delete (CRUD) of Trails in the system
- Implementation of Firebase Auth for basic email/password
- A separate My Trails view which displays only the users own trails
- Remote storage of data on Firebase Realtime Database to replace the previous local JSON storage  to allow multi user functionality
- Storage of images using Firebase Cloud Storage.

**UML / Class Diagram**

![UML Diagram](https://github.com/StephenSwantonIRL/TrailApp2/blob/master/UML.png?raw=true)


## **The Trail & TrailMarker Models**

The data model for this version of the project modified the previous version in a number of ways. Firstly a new *createdBy* field was added to the Trail data class to represent the user creating the trail. Secondly the Trail Markers were separated out from the Trail Class, with the revised Trail data class only containing a list of the unique firebase ids for each marker associated with a trail. Trail Markers therefore also had a new field added to indicate the containing trail. The image references in markers were retyped to strings as they no longer represented URIs of locally stored filed. Finally *uid* fields were added to each data model to allow them to be identified/referenced within the Firebase Realtime Database.


## **Create, Read, Update and Delete / Implementing Firebase Realtime Database**

Create, Read, Update and Delete for markers and trails relied heavily on the following tutorial from the SETU HDip in Computer Science Mobile Application Development module to implement Firebase Realtime database.

*- Lab K10 DonationX-v6*

And also on the following medium.com tutorial:

*- MindOrks - Firebase Realtime Database: Android Tutorial*

As I didn't have sufficient time to implement MVVM I needed to retain the MVC based architecture. Challenges were encountered however when implementing this approach as the realtime database operates asynchronously and without the availability of the LiveData component any operations that needed to performed on the results of an operation needed to be handled within the fragment itself. This is not ideal and if more time was available I would have implemented the MVVM model.

Revisions to the models that decoupled Trail markers from the Trails themselves meant that when deleting trails an additional database call was required to also delete the markers associated with the trail.

## **Nav Drawer & Navigation Component**
The standard Android studio starter wizard was used to create the Nav Drawer for the project and the following youtube video was followed to assist config:

- *Larn Tech 2021 - Android Studio Navigation Drawer With Fragment and Activity || Custom Navigation Drawer*

An error with the status bar overlapping w the navigation drawer was resolved with the solution offered in the following Stack Overflow:

-   Stack Overflow 2016 - Android Navigation Drawer overlap by status bar

Issues with the backstack were overcome with the guidance on NavOptions Builder contained in the following Youtube tutorial.

- Coding with Mitch Youtube 2020 - Resolving problems with NavController and Navigation Graph

Activities from the earlier version of the project were then individually brought into the project and refactored as fragments so that they could be used with the

As the original project already contained some fragments within activities these were refactored as Child fragments.


## **Swipe to Edit and Delete**
This functionality followed the following lab closely - *Lab K09 DonationX-v5* - with modifications owing to the MVC rather than MVVM architecture.


## **User Management**

Firebase Auth was implemented largely based on the following lab -*Lab K09 DonationX-v5*- The JSON store from previous version was removed and new fragments were created following this tutorial.

- Sumit Mishra MindOrks  -  https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial/

An accompanying log out function was developed log the user out. This was housed in a Logout fragment which cleared the back stack once the user was successfully logged out.  
A new fragment was created to only display the trails owned by the user.
This relied on the Firebase dbReference.orderBy("property").equalTo("search-term") to provide the list of trails for the user. (Stack Overflow 2020)

## **Images**

Cloud Storage was implemented to store the images uploaded by users of the app. Implementation followed the method outlined in  *Lab K11 DonationX-v7* with modifications to accommodate the MVC architecture. Two storage folders were created,  one for markers and another for users. As users can only have one profile picture and markers can only have one photo attached the marker the filenames for the stored files was set to correlate with the *uid* for the object related to the image.

To load the images from cloud storage directly into an image view the following Stack Over flow response was referred to.

- StackOverflow 2016 - Load Image from Firebase Storage with Picasso to ImageView in Infowindow, Picasso only shows placeholder

## **Git & Development Approach**

Branches were used for each of the main functionality sets  / architectural refactors as follows:

- *convert-to-fragments* - this was the initial branch which brought the individual activities into the new project as fragments
- *firebase* - this branch contained all development work to enable Firebase Auth and Realtime Database
- *images* - this branch contained all development work related to reintroducing image support from the previous version and implementing Firebase Cloud Storage.
- *swipe-functionality* - this branch handled the implementation of swipe to edit and swipe to delete.
- *my-trails* - handled the my trails fragment and revisions to the list trails to make others trails view only.

**Personal Statement**
Reflecting on the approach taken I think it was a misstep to focus on the introduction of the Firebase support before attempting to implement the architectural revisions but prioritizing firebase meant that I was could manually test the app at all stages rather than attempting the MVVM revisions, breaking the app and then being unable to diagnosis if the issue was firebase or architectural.
I didn't have time to explore how to set up unit testing but there were many points in the project where this would have been incredibly helpful and it could be something worth including in the early portion of the module for future years.

## **References**

Coding with Mitch Youtube 2020 - Resolving problems with NavController and Navigation Graph https://www.youtube.com/watch?v=MTpVJwFROZE

Dave Drohan & Dave Hearne 2022- Mobile Application Development Lab K09 DonationX-v5[https://reader.tutors.dev/#/lab/setu-hdip-comp-sci-2021-mobile-app-dev.netlify.app/topic-10-location/unit-01-dd/book-e-donationx-v5](https://reader.tutors.dev/#/lab/setu-hdip-comp-sci-2021-mobile-app-dev.netlify.app/topic-10-location/unit-01-dd/book-e-donationx-v5)

Dave Drohan & Dave Hearne 2022- Mobile Application Development Lab K10 DonationX-v6
https://reader.tutors.dev/#/lab/setu-hdip-comp-sci-2021-mobile-app-dev.netlify.app/topic-11-rooms/unit-01-dd/book-f-donationx-v6

Dave Drohan & Dave Hearne 2022- Mobile Application Development Lab K11 DonationX-v7 - https://reader.tutors.dev/#/lab/setu-hdip-comp-sci-2021-mobile-app-dev.netlify.app/topic-12-firebase/unit-01-dd/book-g-donationx-v7

Larn Tech 2021 - Android Studio Navigation Drawer With Fragment and Activity || Custom Navigation Drawer - https://www.youtube.com/watch?v=5VsRFJjyMjU

Stack Overflow 2016 - Android Navigation Drawer overlap by status bar  - https://stackoverflow.com/questions/37491093/android-navigationdrawer-overlap-by-status-bar

StackOverflow 2016 - Load Image from Firebase Storage with Picasso to ImageView in Infowindow, Picasso only shows placeholder  - https://stackoverflow.com/questions/39896511/load-image-from-firebase-storage-with-picasso-to-imageview-in-infowindow-picass  
https://firebase.google.com/docs/storage/android/delete-files

Stack Overflow 2020 - How to do a query with that structure in Firebase + Kotlin https://stackoverflow.com/questions/62902283/how-to-do-a-query-with-that-structure-in-firebase-kotlin

Sumit Mishra MindOrks 2019  - Firebase Login and Authentication:    Android Tutorial     https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial/    -

Sumit Mishra - MindOrks 2019 - Firebase Realtime Database tutorial - https://blog.mindorks.com/firebase-realtime-database-android-tutorial/
