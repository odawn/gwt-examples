
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Coming Soon #
> I am building Core-Engine, which is in the source, and follows the oAuth spec for login.

# Login Manger #
> I setup this example to test login manager, to control User access by Username and Password and SessionID. There are many ways you can do this. I followed Google's incubator. You can import my eclipse project or get it via svn. Hope it helps.

> ## Notes ##
  * Google Login Manager Incubator:  http://code.google.com/p/google-web-toolkit-incubator/wiki/LoginSecurityFAQ


> ## Next Version Goal ##
> I have a more advanced version coming in the near future that I have built for another project and will release it here in march. I have been a little to busy to move it all over into its own project.

> ## MySQL Tables Setup ##

> Session Table - store sessions in mysql so it matters not what server rpc request goes too.
```
CREATE TABLE `session` (
  `ID` int(11) NOT NULL auto_increment,
  `UserID` int(11) NOT NULL,
  `SessionID` varchar(200) NOT NULL,
  `LastAccessed` int(11) NOT NULL COMMENT 'unix timestamp',
  `DateCreated` int(11) NOT NULL COMMENT 'unix timestamp',
  PRIMARY KEY  (`ID`),
  KEY `SessionID` (`SessionID`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1
```

> User Table - user identity, I am using Email as Username, but thats easy to change
```
CREATE TABLE `user` (
  `UserID` int(11) NOT NULL auto_increment,
  `NickName` varchar(50) default NULL,
  `FirstName` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `UserName` varchar(150) NOT NULL,
  `Password_Hash` varchar(255) NOT NULL,
  `DateCreated` int(11) NOT NULL,
  `LastUpdated` int(11) default '0',
  PRIMARY KEY  (`UserID`),
  KEY `user` (`UserName`),
  KEY `password` (`Password_Hash`),
  KEY `userpass` (`UserName`,`Password_Hash`)
) ENGINE=MyISAM AUTO_INCREMENT=39 DEFAULT CHARSET=latin1
```

> Email Table - user emails
```
CREATE TABLE `email` (
  `EmailID` int(11) NOT NULL auto_increment,
  `UserID` int(11) NOT NULL,
  `Email` varchar(200) NOT NULL,
  `DateCreated` int(11) NOT NULL,
  `LastUpdated` int(11) default NULL,
  PRIMARY KEY  (`EmailID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1
```

> User Personal Info Table - collect some things about the user
```
CREATE TABLE `user_personal` (
  `PersonalID` int(11) NOT NULL auto_increment,
  `UserID` int(11) NOT NULL default '0',
  `BirthMonth` int(11) default '0',
  `BirthDay` int(11) default '0',
  `BirthYear` int(11) default '0',
  `Gender` tinyint(4) default '0',
  `Zip` int(11) default '0',
  `TimeZone` varchar(10) NOT NULL,
  `DateCreated` int(11) NOT NULL default '0',
  `LastUpdated` int(11) default '0',
  PRIMARY KEY  (`PersonalID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1
```

> ## JDBC ##
> Remember to add JDBC to the build path. More info here [gwtEclipseFaqs](gwtEclipseFaqs.md)

> &lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
