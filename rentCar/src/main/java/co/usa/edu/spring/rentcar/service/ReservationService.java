/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.usa.edu.spring.rentcar.service;

import co.usa.edu.spring.rentcar.crudrepository.repository.ReservationRepository;
import co.usa.edu.spring.rentcar.model.Reservation;
import co.usa.edu.spring.rentcar.model.custom.CountClient;
import co.usa.edu.spring.rentcar.model.custom.StatusAmount;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase que permte el acceso a los métodos de comprobación de las peticiones HTTP
 * GET, POST, PUT, DELETE que provienen del Controlador 
 * @author CARLOS ANDRES
 */

@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservaRepository;
    /**
     * Método que permite recuperar las reservaciones en la BD
     * @return una lista de todas las reservaciones en la BD 
     */
    public List<Reservation> getAllReservations(){
        return (List<Reservation>) reservaRepository.getAll();
    }
    /**
     * Método que permite recuperar una reservación por clave principal
     * @param id clave principal en la tabla de reservaciones
     * @return un reservación específica por Id
     */
    public Optional<Reservation> getReservationById(int id){
        return reservaRepository.getReservation(id);
    }
    /**
     * Método que realiza la comprobación de existencia de la reserva en la BD
     * permitiendo guardarla si no existe ya en la BD
     * @param res objeto Reservation que se compone de id, startDate, devDate, status
     * @return un objeto Reservation con todos sus atributos 
     */
    public Reservation saveReservation(Reservation res){
        if(res.getIdReservation()==null){
            return reservaRepository.save(res);
        }else{
            Optional<Reservation> reservaOpt = reservaRepository.getReservation(res.getIdReservation());
            if (!reservaOpt.isPresent()){
                return reservaRepository.save(res);
            }else{
                return res;
            }
        }
    }
    /**
     * Método que permite actualizar una reserva siempre y cuando el objeto 
     * Reservation exita en la BD se comprueba que cada parámetro este dentro del 
     * objeto
     * @param res objeto Reservation que se compone de id, startDate, devDate, status
     * @return un objeto Reservation con todos sus atributos
     */
    public Reservation updateCar(Reservation res){
        if(res.getIdReservation()!=null){
            Optional<Reservation> resOpt = reservaRepository.getReservation(res.getIdReservation());
            if(resOpt.isPresent()){
                if(res.getStartDate()!=null){
                    resOpt.get().setStartDate(res.getStartDate());
                }
                if(res.getDevolutionDate()!=null){
                    resOpt.get().setDevolutionDate(res.getDevolutionDate());
                }
                if(res.getStatus()!=null){
                    resOpt.get().setStatus(res.getStatus());
                }
                return reservaRepository.save(resOpt.get());
            }
        }
        return res;
    }
    /**
     * Método que permite la comprobación de la existencia de un registro de reserva
     * en la BD, eliminandolo dependiendo de ello
     * @param id parámetro clave para la comprobación de existencia en la BD
     * @return boleano que indica si la operación se realizó o no
     */
    public boolean deleteReservation(int id){
        Optional<Reservation> resOpt = getReservationById(id);
        if(resOpt.isPresent()){
            reservaRepository.delete(resOpt.get());
            return true;
        }
        return false;
    }
    /*
    public List<Reservation> getResevByDateRange(Date startDate, Date devDate){
        return reservaRepository.getByDateRange(startDate, devDate);
    }
    
    public List<Object> getBySatus(){
       return reservaRepository.getCountByStatus();
    }
    */
    
    public List<Reservation> getResByDateRange(String startD, String endD){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        Date start = new Date();
        Date end = new Date();
        try{
            start = formater.parse(startD);
            end = formater.parse(endD);
        }catch(ParseException err){
            err.printStackTrace();
        }
        if (start.before(end)){
        return reservaRepository.getResByDateRange(start, end);
        }else{
            return new ArrayList<>();
        }
    }
    
   public StatusAmount getStatusReport(){
       List<Reservation> completed = reservaRepository.getTotalByStatus("completed");
       List<Reservation> cancelled = reservaRepository.getTotalByStatus("cancelled");
       StatusAmount statusAmnt = new StatusAmount(completed.size(), cancelled.size());
       
       return statusAmnt;
   }
    
    public List<CountClient> getTopReservClients(){
        return reservaRepository.getTopReservClient();
    }
}
