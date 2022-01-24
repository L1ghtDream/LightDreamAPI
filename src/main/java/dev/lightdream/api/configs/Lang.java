package dev.lightdream.api.configs;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

@SuppressWarnings("CanBeFinal")
public class Lang {

    public String mustBeAPlayer = "You must be a player to use this command.";
    public String mustBeConsole = "You must be console to use this command.";
    public String noPermission = "You do not have the permission to use this command.";
    public String unknownCommand = "This is not a valid command.";
    @SuppressWarnings("unused")
    public String invalidUser = "This is not a valid user.";
    @SuppressWarnings("unused")
    public String invalidNumber = "This is not a valid number";
    public String version = "%project_name% version %version%";
    public String invalidLang = "This is not a valid lang";
    public String langChanged = "Language changed";
    public String helpCommand = "";
    public String pluginList = "Plugins: %plugins%";
    public String pluginFormat = "%project-name%(%project-id%) %project-version%";
    @SuppressWarnings("unused")
    public String offlineUser = "The user is offline";

    @SuppressWarnings("unused")
    @SneakyThrows
    public String getFieldValue(Field searchField) {
        if (searchField == null) {
            return "";
        }
        for (Field field : this.getClass().getFields()) {
            if (field.getName().equals(searchField.getName())) {
                return (String) field.get(this);
            }
        }
        return "";
    }

    @SuppressWarnings("unused")
    @SneakyThrows
    public Field getFieldName(String search) {
        for (Field field : this.getClass().getFields()) {
            if (field.get(this).equals(search)) {
                return field;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "Lang{" + "mustBeAPlayer='" + mustBeAPlayer + '\'' + ", mustBeConsole='" + mustBeConsole + '\'' + ", noPermission='" + noPermission + '\'' + ", unknownCommand='" + unknownCommand + '\'' + ", invalidUser='" + invalidUser + '\'' + ", invalidNumber='" + invalidNumber + '\'' + ", version='" + version + '\'' + ", invalidLang='" + invalidLang + '\'' + ", langChanged='" + langChanged + '\'' + ", helpCommand='" + helpCommand + '\'' + '}';
    }
}
