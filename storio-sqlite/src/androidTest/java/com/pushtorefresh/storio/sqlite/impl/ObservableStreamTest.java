package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ObservableStreamTest extends BaseSubscriptionTest {

    private class EmissionChecker extends BaseEmissionChecker<List<User>> {

        public EmissionChecker(@NonNull Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Subscription subscribe() {
            return storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(UserTableMeta.QUERY_ALL)
                    .prepare()
                    .createObservable()
                    .subscribe(new Action1<List<User>>() {
                        @Override
                        public void call(List<User> users) {
                            onNextObtained(users);
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> initialUsers = putUsersBlocking(10);
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(initialUsers.size() + usersForInsert.size());

        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(allUsers);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        putUsersBlocking(usersForInsert);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsersBlocking(10);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        final List<User> updatedList = new ArrayList<User>(users.size());

        int count = 1;
        for (User user : users) {
            updatedList.add(User.newInstance(user.id(), "new_email" + count++));
        }
        expectedUsers.add(users);
        expectedUsers.add(updatedList);
        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        storIOSQLite
                .put()
                .objects(User.class, updatedList)
                .prepare()
                .executeAsBlocking();

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> usersThatShouldBeSaved = TestFactory.newUsers(10);
        final List<User> usersThatShouldBeRemoved = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>();

        allUsers.addAll(usersThatShouldBeSaved);
        allUsers.addAll(usersThatShouldBeRemoved);

        putUsersBlocking(allUsers);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        expectedUsers.add(allUsers);
        expectedUsers.add(usersThatShouldBeSaved);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        deleteUsersBlocking(usersThatShouldBeRemoved);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }
}
