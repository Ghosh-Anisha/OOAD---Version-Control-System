/*Building a Simple version control system to Add, Commit, Remove, Branch and log a repository 
creates a working directory on local system and manges it*/

public class Main {

    public static void main(String... args) {

        Repo r = new Repo();
        Boolean valid = false; 
        int inputLength = args.length;
        if (inputLength == 0) {
            System.out.println("Please enter a command.");
        } 
        else {
            switch (args[0]) {
                case "init": {
                    //initialises a new repository with all the base directories required to perform version control system 
                    if(args[1].equals("user") && args[2].equals("user"))
                    {
                        valid = true;
                        if (inputChecker(3, args)) {
                            r.init();
                        }
                    }
                    else
                    {
                        System.out.println("Authentication failed");
                    }  
                    break;
                }
                case "add": {
                    //Add a file to the current working directory 
                    if (inputChecker(2, args) && valid) {
                        r.add(args[1]);
                    }
                    break;
                }
                case "commit": {
                    //commit new changes such as add and remove 
                    if (inputChecker(2, args)&& valid) {
                        r.commitment(args[1]);
                    }
                    break;
                }
                case "rm": {
                    //remove a file that was previously committed 
                    if (inputChecker(2, args) && valid) {
                        r.rm(args[1]);
                    }
                    break;
                }
                case "log":
                    //check previous commit history 
                    if (inputChecker(1, args) && valid) {
                        r.log();
                    }
                    break;
                case "global-log":
                    if (inputChecker(1, args) && valid) {
                        r.global();
                    }
                    break;
                case "branch": {
                    //creates a new branch 
                    if (inputChecker(2, args) && valid) {
                        String branchName = args[1];
                        r.branch(branchName);
                    }
                    break;
                }
                case "rm-branch": {
                    if (inputChecker(2, args) && valid) {
                        String branchName = args[1];
                        r.rmb(branchName);
                    }
                    break;
                }

                default:
                    System.out.println("No command with that name exists.");
            }
        }
        System.exit(0);
    }

    static boolean inputChecker(int length, String... args) {
        if (args.length == length) {
            return true;
        }
        System.out.println("Incorrect Operands");
        return false;
    }
}