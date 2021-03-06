package com.codegym.laptopmanager.controller;

import com.codegym.laptopmanager.model.*;
import com.codegym.laptopmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
@PropertySource("classpath:global_config_app.properties")
@RequestMapping("/laptop")
public class LaptopController {
    @Autowired
    Environment environment;

    @Autowired
    private ILaptopService laptopService;
    @Autowired
    private IStatusService statusService;
    @Autowired
    private IOrdersService ordersService;
    @Autowired
    private IProducerService producerService;

    @Autowired
    private ICustomerService customerService;

    @ModelAttribute("statuses")
    public Iterable<Status> statuses(){
        return statusService.findAll();
    }
    @ModelAttribute("orders")
    public Page<Orders> orders(Pageable pageable){
        return ordersService.findAll(pageable);
    }
    @ModelAttribute("producer")
    public Page<Producer> producers(Pageable pageable){
        return producerService.findAll(pageable);
    }

    @ModelAttribute("customers")
    public Page<Customer> customers(Pageable pageable){
        return customerService.findAll(pageable);
    }

    @GetMapping
    public ModelAndView listLaptop(@RequestParam("laptop") Optional<String> name,
                                     @RequestParam(required = false) Long statusId,
                                     @PageableDefault(size = 3) Pageable pageable) {
        Page<Laptop> laptops;
        if (name.isPresent()) {
            laptops = laptopService.findAllByName(name.get(), pageable);
        } else {
            laptops = laptopService.findAll(pageable);
        }

        Optional<Status> status = Objects.nonNull(statusId)
                ? statusService.findById(statusId)
                : Optional.empty();
        if (status.isPresent()) {
            laptops = new PageImpl<>(status.get().getLaptops());
        }

            ModelAndView modelAndView = new ModelAndView("laptop/list");
            modelAndView.addObject("laptops", laptops);
            return modelAndView;
        }


    @GetMapping("/create")
    public ModelAndView showFormCreateLaptop(){
        ModelAndView modelAndView = new ModelAndView("laptop/create");
        modelAndView.addObject("laptopForm", new LaptopForm());
        return modelAndView;
    }

    @PostMapping("/create")
    public RedirectView saveLaptop(@ModelAttribute("laptopForm") LaptopForm laptopForm, BindingResult bindingResult, RedirectAttributes redirect){
        if (bindingResult.hasErrors()){
            System.out.println("Result Error Occured" + bindingResult.getAllErrors());
        }
        //Lay ten file
        MultipartFile multipartFile = laptopForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = environment.getProperty("file_upload").toString();

        //Luu file len server
        try {
            FileCopyUtils.copy(laptopForm.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Tao doi tuong de luu vao database
        Laptop laptop = new Laptop(laptopForm.getName(),fileName, laptopForm.getDescription(), laptopForm.getPrice(), laptopForm.getStatus(), laptopForm.getOrders(), laptopForm.getProducer(), laptopForm.getCustomer());
        laptopService.save(laptop);
        redirect.addFlashAttribute("message", "create laptop successfully !");
        return new RedirectView("/laptop");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditLaptop(@PathVariable Long id){
        Optional<Laptop> laptop = laptopService.findById(id);

        LaptopForm laptopForm = new LaptopForm();
        if (laptop.isPresent()){
        laptopForm.setId(laptop.get().getId());
        laptopForm.setName(laptop.get().getName());
        laptopForm.setDescription(laptop.get().getDescription());
        laptopForm.setPrice(laptop.get().getPrice());
        laptopForm.setStatus(laptop.get().getStatus());
        laptopForm.setOrders(laptop.get().getOrders());
        laptopForm.setProducer(laptop.get().getProducer());
        laptopForm.setCustomer(laptop.get().getCustomer());

        ModelAndView modelAndView = new ModelAndView("/laptop/edit");
        modelAndView.addObject("laptopForm", laptopForm);
        return modelAndView;
        } else {
            return new ModelAndView("/laptop/error");
        }
    }

    @PostMapping("/edit")
    public RedirectView updateLaptop(@ModelAttribute("laptopForm") LaptopForm laptopForm, BindingResult bindingResult, RedirectAttributes redirect){
        if (bindingResult.hasErrors()){
            System.out.println("Result Error Occured" + bindingResult.getAllErrors());
        }
        MultipartFile multipartFile = laptopForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = environment.getProperty("file_upload").toString();

        if (fileName.equals("")){
            Laptop laptop = new Laptop(laptopForm.getId(), laptopForm.getName(),fileName, laptopForm.getDescription(), laptopForm.getPrice(), laptopForm.getStatus(), laptopForm.getOrders(), laptopForm.getProducer(), laptopForm.getCustomer());
            laptopService.save(laptop);
            redirect.addFlashAttribute("message","edit laptop error !");
            return new RedirectView("/laptop");
        }
        else {
            try {
                FileCopyUtils.copy(laptopForm.getImage().getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Laptop laptop = new Laptop(laptopForm.getId(), laptopForm.getName(),fileName, laptopForm.getDescription(), laptopForm.getPrice(), laptopForm.getStatus(), laptopForm.getOrders(), laptopForm.getProducer(), laptopForm.getCustomer());
            laptopService.save(laptop);
            redirect.addFlashAttribute("message","edit laptop successfully !");
            return new RedirectView("/laptop");
        }
    }

    @GetMapping("/delete/{id}")
    public ModelAndView showFormDeleteLaptop(@PathVariable Long id) {
        Optional<Laptop> laptop = laptopService.findById(id);
        if (laptop.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/laptop/delete");
            modelAndView.addObject("laptop", laptop.get());
            return modelAndView;
        } else {
            return new ModelAndView("/laptop/error");
        }
    }

    @PostMapping("/delete")
    public RedirectView deleteLaptop(@ModelAttribute("laptop") Laptop laptop, RedirectAttributes redirect){
        laptopService.remote(laptop.getId());
        redirect.addFlashAttribute("message", "delete laptop successfully !");
        return new RedirectView("/laptop");
    }

    @GetMapping("/view/{id}")
    public ModelAndView viewLaptop(@PathVariable Long id){
        Optional<Laptop> laptop = laptopService.findById(id);
        if (laptop.isPresent()){
            ModelAndView modelAndView = new ModelAndView("/laptop/view");
            modelAndView.addObject("laptop", laptop.get());
            return modelAndView;
        }
        else {
            return new ModelAndView("/laptop/error");
        }
    }
}
