package com.pingtech.hgqw.readcard.utils;

import android_serialport_api.ParseSFZAPI.People;

import com.softsz.deviceInterface.PersonData;

public class Pa8Utils {

	public static People rebuildPersonData(PersonData data) {
		People people = new People();
		people.setPeopleName(data.getName());
		people.setPeopleIDCode(data.getIDCard());
		people.setPeopleAddress(data.getAddress());
		people.setPeopleBirthday(data.getBirthYearMonthDay());
		people.setPeopleNation(data.getNation());
		people.setPeopleSex(data.getSexCode());
		people.setPhotoPath(data.getPhotoPath());
		people.setStartDate(data.getBeginDay());
		people.setEndDate(data.getEndDay());
		return people;
	}

}
