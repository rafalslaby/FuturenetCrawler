class Person
{
    String label;
    int cid;
    int male;
    String lang;
    int data_user;
    String name;

    static String getFriendsListLink(String label){
        return "https://kajak22.futurenet.club/u/" + label + "/friends";
    }

}