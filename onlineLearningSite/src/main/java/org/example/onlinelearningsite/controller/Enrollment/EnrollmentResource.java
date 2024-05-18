package org.example.onlinelearningsite.controller.Enrollment;

import org.example.onlinelearningsite.Entity.Enrollment;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/enrollments")
public class EnrollmentResource {

    @EJB
    private EnrollmentController enrollmentService;

    @OPTIONS
    @Path("{path: .*}")
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enrollStudent(Enrollment enrollment) {
        try {
            enrollmentService.enrollStudent(enrollment);
            return Response.ok("Student enrolled successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to enroll student: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/cancel/{studentEmail}")
    public Response cancelEnrollment(@PathParam("studentEmail") String studentEmail) {
        try {
            enrollmentService.cancelEnrollment(studentEmail);
            return Response.ok("Enrollment canceled successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to cancel enrollment: " + e.getMessage()).build();
        }
    }
    @GET
    @Path("/instructor/{instructorEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEnrollmentsForInstructor(@PathParam("instructorEmail") String instructorEmail) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForInstructor(instructorEmail);
        return Response.ok(enrollments).build();
    }

    @GET
    @Path("/student/current/{studentEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentEnrollmentsForStudent(@PathParam("studentEmail") String studentEmail) {
        try {
            List<Enrollment> enrollments = enrollmentService.getCurrentEnrollmentsForStudent(studentEmail);
            return Response.ok(enrollments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve current enrollments for student: " + e.getMessage()).build();
        }
    }
    @GET
    @Path("/student/past/{studentEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPastEnrollmentsForStudent(@PathParam("studentEmail") String studentEmail) {
        try {
            List<Enrollment> enrollments = enrollmentService.getPastEnrollmentsForStudent(studentEmail);
            return Response.ok(enrollments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve past enrollments for student: " + e.getMessage()).build();
        }
    }


    @PUT
    @Path("/{enrollmentId}/approve")
    public Response approveEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        try {
            enrollmentService.approveEnrollment(enrollmentId);
            return Response.ok("Enrollment approved successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to approve enrollment: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{enrollmentId}/reject")
    public Response rejectEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        try {
            enrollmentService.rejectEnrollment(enrollmentId);
            return Response.ok("Enrollment rejected successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to reject enrollment: " + e.getMessage()).build();
        }
    }
}
