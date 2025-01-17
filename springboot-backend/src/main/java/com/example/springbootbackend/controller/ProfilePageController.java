package com.example.springbootbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootbackend.exception.ResourceNotFoundException;
import com.example.springbootbackend.model.User;
import com.example.springbootbackend.model.Ticket;
import com.example.springbootbackend.model.Event;
import com.example.springbootbackend.repository.UserRepository;
import com.example.springbootbackend.repository.TicketRepository;
import com.example.springbootbackend.repository.EventRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/profile/")
public class ProfilePageController {

	@Autowired
	private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;
	
	// get user. user contains set of tickets.
    // TODO: may not want the username in the url. try to find another way to pass the information.
	// May also want to check that the user is verified before returning the data.
    @GetMapping("{email}")
	public ResponseEntity<Map<User, Set<Ticket>>> getUser(@PathVariable String email){
        User user = userRepository.findById(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not exist with email :" + email));
        Map<User, Set<Ticket>> response = new HashMap<>();
        response.put(user, user.getTickets());
        return ResponseEntity.ok(response);
	}

    // delete employee rest api
    // TODO: this isn't tested since we need to be able to create a delete request
    // and simply navigating to the url won't trigger this method
	@DeleteMapping("{email}/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteTicket(@PathVariable String email, @PathVariable Long id){
		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not exist with id :" + id));
        User user = userRepository.findById(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with email :" + email));

        if(user.hasTicket(ticket)){
            // Remove the ticket from the user
            user.removeTicket(ticket); // TODO: check removal is a success
            // Update event: 
            Event event = ticket.getEvent();
            String ticket_type = ticket.getTicketType();
            if(ticket_type.equals("vip")){
                event.setVipTicketsLeft(event.getVipTicketsLeft() + 1);
            }
            else if(ticket_type.equals("floor")){
                event.setFloorTicketsLeft(event.getFloorTicketsLeft() + 1);
            }
            else if(ticket_type.equals("genad")){
                event.setGenadTicketsLeft(event.getGenadTicketsLeft() + 1);
            }
            eventRepository.save(event);
            userRepository.save(user);
            ticketRepository.delete(ticket);

            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        }
        else{
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.FALSE);
            return ResponseEntity.ok(response);
        }
	}
}