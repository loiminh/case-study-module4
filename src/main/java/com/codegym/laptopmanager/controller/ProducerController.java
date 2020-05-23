package com.codegym.laptopmanager.controller;

import com.codegym.laptopmanager.model.Producer;
import com.codegym.laptopmanager.service.IProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/producer")
public class ProducerController {
    @Autowired
    private IProducerService producerService;

    @GetMapping
    public ModelAndView listProducer(@RequestParam("name") Optional<String> name, @PageableDefault(size = 10)Pageable pageable){
        Page<Producer> producers;
        if (name.isPresent()){
            producers = producerService.findAllByName(name.get(), pageable);
        }else {
            producers = producerService.findAll(pageable);
        }
        ModelAndView modelAndView = new ModelAndView("producer/list");
        modelAndView.addObject("producers", producers);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView showFormCreateProducer(){
        ModelAndView modelAndView = new ModelAndView("/producer/create");
        modelAndView.addObject("producer", new Producer());
        return modelAndView;
    }

    @PostMapping
    public String saveProducer(@ModelAttribute("producer") Producer producer, RedirectAttributes redirect){
        producerService.save(producer);
        redirect.addFlashAttribute("message", "Create producer successfully !");
        return "redirect:/producer";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditProducer(@PathVariable Long id){
        Optional<Producer> producer = producerService.findById(id);
        if (producer.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/producer/edit");
            modelAndView.addObject("producer", producer.get());
            return modelAndView;
        }else {
            return new ModelAndView("/producer/error");
        }
    }

    @PostMapping("/edit")
    public String editProducer(@ModelAttribute("producer") Producer producer, RedirectAttributes redirect){
        producerService.save(producer);
        redirect.addFlashAttribute("message", "Edit producer successfully !" );
        return "redirect:/producer";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView showFormDeleteProducer(@PathVariable Long id){
        Optional<Producer> producer = producerService.findById(id);
        if (producer.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/producer/delete");
            modelAndView.addObject("producer", producer.get());
            return modelAndView;
        }else {
            return new ModelAndView("/producer/error");
        }
    }

    @PostMapping("/delete")
    public String deleteProducer(@ModelAttribute("producer") Producer producer, RedirectAttributes redirect){
        producerService.remote(producer.getId());
        redirect.addFlashAttribute("message", "Delete producer successfully !" );
        return "redirect:/producer";
    }

    @GetMapping("/view/{id}")
    public ModelAndView viewProducer(@PathVariable Long id){
        Optional<Producer> producer = producerService.findById(id);
        if (producer.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/producer/view");
            modelAndView.addObject("producer", producer);
            return modelAndView;
        }
        return new ModelAndView("/producer/error");
    }





}
