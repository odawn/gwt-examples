
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# Using Google Fonts In GWT #
> If your using one of the standard or other style sheets included in the project.gwt.xml file, you may get some conflicts with the css style sheets, as to the inheritance and which one is setting precedence.

> I (copy) add my css files to [project.gwt.xml location]/public/css/standard/standard.css
```
  <!-- include style sheets in project.gwt.xml file -->
  <!-- ref: ./src/com/gonevertical/GoneVertical.gwt.xml or ./src/com/gonevertical/public/gwt/css/fonts.css -->
  <!-- css - the relative root directory is that of the project.gwt.xml directory -->
  <stylesheet src="gwt/css/fonts.css"/>  
  <stylesheet src="gwt/css/standard/standard.css"/>
  <stylesheet src="gwt/css/Gawkat.css"/> 
```

> Insead of using the link tag, I include it like this in fonts.css and is included in the project xml module file configuration.
```
/* fonts.css file - this is retrieved from the google fonts link */
/* it was retrieved from <link href='http://fonts.googleapis.com/css?family=Ubuntu&v1' rel='stylesheet' type='text/css'> */
@font-face {
  font-family: 'Ubuntu';
  font-style: normal;
  font-weight: bold;
  src: local('Ubuntu Bold'), local('Ubuntu-Bold'), url('http://themes.googleusercontent.com/font?kit=0ihfXUL2emPh0ROJezvraLO3LdcAZYWl9Si6vvxL-qU') format('woff');
}
@font-face {
  font-family: 'Ubuntu';
  font-style: normal;
  font-weight: normal;
  src: local('Ubuntu'), url('http://themes.googleusercontent.com/font?kit=vRvZYZlUaogOuHbBTT1SNevvDin1pK8aKteLpeZ5c0A') format('woff');
}
@font-face {
  font-family: 'Ubuntu';
  font-style: italic;
  font-weight: normal;
  src: local('Ubuntu Italic'), local('Ubuntu-Italic'), url('http://themes.googleusercontent.com/font?kit=kbP_6ONYVgE-bLa9ZRbvvnYhjbSpvc47ee6xR_80Hnw') format('woff');
}
@font-face {
  font-family: 'Ubuntu';
  font-style: italic;
  font-weight: bold;
  src: local('Ubuntu Bold Italic'), local('Ubuntu-BoldItalic'), url('http://themes.googleusercontent.com/font?kit=OMD20Sg9RTs7sUORCEN-7brIa-7acMAeDBVuclsi6Gc') format('woff');
}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
