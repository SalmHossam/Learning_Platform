from enum import Enum
class Role(Enum):
    ADMIN = "admin"
    INSTRUCTOR="instructor"
    STUDENT = "student"
class Users:
    def __init__(self, userId,name,role,email,password,
                 affliaction,bio,yearsOfExperience,createdAt,updatedAt):
        self.userId = userId
        self.name= name
        self.role=role
        self.email=email
        self.password=password
        self.affiliaction=affliaction
        self.bio=bio
        self.yearOfExperience=yearsOfExperience
        self.createdAt=createdAt
        self.updatedAt= updatedAt
