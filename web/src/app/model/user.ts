export type User = {
    username: string;
    email: string;
    authorities: string[];
};

export type NewUser = {
  username: string;
  email: string;
  password: string;
  matchingPassword: string;
};