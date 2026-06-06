package com.example.myapplication;

public interface MainScreenHost {
    void setScreenHeader(String title, String subtitle);

    void goToTab(int menuItemId);

    void openTeamSelect();

    void logout();
}
