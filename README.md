ListMatcher
=========

Library for performing denylist/allowlist matching operations for things like email addresses.


Maven Dependency:

```xml
<dependency>
  <groupId>com.giladam</groupId>
  <artifactId>ListMatch</artifactId>
  <version>1.1.0</version>
</dependency>
```


Example usage for managing single list:

```java
    
    //load some patterns from a text file:
    Set<String> allowListPatterns = ListMatcher.readPatternsFromFile(new File("/tmp/allow.txt"));

    PatternList patternList = new PatternList(allowListPatterns, "@", false);
        
    //do some matching:
    boolean inAllowlist = patternList.matches("test@example.com");
```


Example usage for managing multiple lists:

```java
    
    //load some patterns from a text file:
    Set<String> allowListPatterns = ListMatcher.readPatternsFromFile(new File("/tmp/someAllowlist.txt"));
    Set<String> denyListPatterns = ListMatcher.readPatternsFromFile(new File("/tmp/someDenylist.txt"));

    //some options
    boolean caseSensitive = false;

    //If you have multiple lists to manage:
    Map<String,PatternList> listByName = new HashMap<>();

    listByName.put("email.allowlist", new PatternList(allowListPatterns, "@", caseSensitive));
    listByName.put("email.denylist", new PatternList(denyListPatterns, "@", caseSensitive));

    ListMatcher listMatcher = new ListMatcher(listByName);
    
    //do some matching:
    boolean inAllowlist = listMatcher.matchesList("email.allowlist", "test@example.com");
    boolean inDenylist = listMatcher.matchesList("email.denylist", "test@example.com");
```



Matching patterns can look like (example using '@' delimiter for an email patterns list):

```

# Wildcard as email local part:
*@anylocalpart.com

# Wildcard as email domain part
anydomain@*

# Exact matches are preferred because they are tested against directly:
user@domain.com

# Wildcard after some characters:
startswith*@domain.com

# Wildcard before some characters:
*endswith@domain.com
```


Matching patterns can also look like (example using '.' delimiter for an ip address list, but I think there are probably
better libraries out there for IP range handling.):


```

# Exact match
10.100.0.1

# Wildcards in IP ranges:
10.*.*.*

# Wildcards in IP ranges:
10.100.0.*

```



Matching patterns can also have no delimiter for components like (example using "" or null for delimiter for an url list):

```

# Exact match
https://www.example.com/specific.html

# Wildcards in urls:
http://www.example.com/allowed/*

# Wildcards in the middle and end:
http://*.edu/*

```
