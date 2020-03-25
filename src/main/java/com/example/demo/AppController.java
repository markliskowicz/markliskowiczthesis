package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import FBO.FilenameURLPair;
import FBO.IDBodyPair;
import FBO.PostFBO;
import FBO.SMPost;
import FBO.StoredSMPost;



@Controller
public class AppController {
	
	public AppController() {
		//fileDao = new FileDao();
		//userDao = new UserDao();
		//postDao = new SMPostDao();
		
		//facebookPoster = new FacebookPoster(fileDao);
		//twitterPin = new TwitterPin();
	}

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private FileDao fileDao;
	
	@Autowired
	private StorageService storageService;
	
	private Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	
	@Autowired	
	private UserDao userDao;
	
	@Autowired
	private SMPostDao postDao;
	
	private String ownerName ;//= authentication.getName();
	private int owner = -1;//userDao.getID(ownerName);
	
	private StoredSMPost currentPost;
	
	//@Autowired
	private TwitterPoster twitterPoster;
	
	//@Autowired 
	private FacebookPoster facebookPoster;
	
	//@Autowired
	private InstagramPoster instagramPoster;
	
	//@Autowired 
	private TwitterPin twitterPin;

	@GetMapping(value = "/")
	public String postHomeFromRoot(Principal principal) {
		getUserIdOnStart(principal);
		System.out.println("got to postHome()");
		return "home";
	}

	private void getUserIdOnStart(Principal principal) {
		if(principal != null && owner == -1) {
			ownerName = principal.getName();
			System.out.println(ownerName);
			owner = userDao.getID(ownerName);
		}
	}
	
	@GetMapping(value = "/home")
	public String postHome(Principal principal) {
		//System.out.println("got to postHome()");
		getUserIdOnStart(principal);
		return "home";
	}
	
	@GetMapping(value = "/savedPosts")
	public String handleSearch(@RequestParam(name = "id", required=false, defaultValue="0") String id, Model model) {
		if(id.equals("0")) {
			ArrayList<IDBodyPair> pairs = postDao.getPostIDs(owner);
			model.addAttribute("posts", pairs);
			return "posts";
		} else {
			//find the product with the matching id
			StoredSMPost post = postDao.getPostbyID(Integer.parseInt(id));
			currentPost = post;
			if(post == null) {
				ArrayList<IDBodyPair> pairs = postDao.getPostIDs(owner);
				model.addAttribute("posts", pairs);
				return "posts";
			}
			model.addAttribute("post", post);
			return "savedPost";
		}		
	}
	
	@GetMapping(value = "/savedPost")
	public String showSavedPost(StoredSMPost post, Model model) throws IOException {
		model.addAttribute("post", currentPost);
		return "savedPost";
	}
	
	@GetMapping(value = "/restore")
	public String restoreSavedPost(Model model) throws IOException {
		String sites = currentPost.getWebsite();
		if(sites.charAt(0) == '1') {
    		//twitterPoster = new TwitterPoster();
    		
    		twitterPoster.restore(currentPost);
    	}
    	if(sites.charAt(1) == '1') {
    		//InstagramPoster instagramPoster = new InstagramPoster();
    		instagramPoster.restore(currentPost);
    	}
    	if(sites.charAt(2) == '1') {
    		//FacebookPoster facebookPoster = new FacebookPoster();
    		facebookPoster.restore(currentPost);
    	}
		return "confirmation";
	}
	
	@GetMapping(value = "/createAccount")
	public String showCreateAccount(Account account, Model model) throws IOException {
		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAgot to showCreateAccount()");
		model.addAttribute("account", new Account());
		return "createAccount";
	}

	@PostMapping(value = "/createAccount")
	public String createAccount(@Valid @ModelAttribute("account") Account account, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "createAccount";
		} else {
			model.addAttribute(account);
			if(!account.getPassword().equals(account.getConfirmPassword())){
				return "createAccount";
			}
			account.setRole("user");
			account.setConfirmPassword(encodePassword(account.getConfirmPassword()));
			account.setPassword(encodePassword(account.getPassword()));
			accountDao.createAccount(account);
			return "home";
		}
	}

	private String encodePassword(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

	@RequestMapping(value = "/showLogout", method = RequestMethod.GET)
	public String showLogoutForm() {
		return "logout";
	}
	
	/*
	 * @Autowired public AppController(StorageService storageService) {
	 * this.storageService = storageService; }
	 */

    @GetMapping("/viewFiles")
    public String listUploadedFiles(Model model) throws IOException {
    	System.out.println("got to listUploadedFiles()");
		/*
		 * model.addAttribute("files", storageService.loadAll().map( path ->
		 * MvcUriComponentsBuilder.fromMethodName(AppController.class, "serveFile",
		 * path.getFileName().toString()).build().toString())
		 * .collect(Collectors.toList()));
		 */
    	//FileDao fileDao = new FileDao();
    	ArrayList<FilenameURLPair> listOfFiles = fileDao.getFileNamesAndURLs(owner);
    	model.addAttribute("files", listOfFiles);
        return "UploadFile";
       
    }

    @PostMapping("/viewFiles")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
    	System.out.println("got to handleFileUpload()");
    	//FileDao fileDao = new FileDao();
    	//storageService.store(file);
    	CloudinaryUploader uploader = new CloudinaryUploader(fileDao);
    	uploader.saveFile(file, owner, fileDao); 
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/viewFiles";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/createPost")
    public String handlePostUpload(HttpServletRequest servletRequest, 
            @ModelAttribute PostFBO post,
            Model model)  {
    	// need seperate post fbo
    	boolean instagramPostResult = true;
    	boolean facebookPostResult = true;
    	boolean twitterPostResult = true;
        SMPost smpost = new SMPost();
        smpost.setStoredPhotoURL(url);
        CloudinaryUploader uploader = new CloudinaryUploader(fileDao);
        model.addAttribute("post", post);
        smpost.setBody(post.getBody());
        smpost.setOwner(owner);
        String website = "";
    	if(post.isPostToTwitter()) {
    		//twitterPoster = new TwitterPoster();
    		website = website + "1";
    		if(twitterPoster != null) {
    			twitterPoster.post(smpost);
    		}
    	} else {
    		website = website + "0";
    	}
    	if(post.isPostToInstagram()) {
    		//InstagramPoster instagramPoster = new InstagramPoster();
    		website = website + "1";
    		if(instagramPoster != null) {
    			instagramPostResult = instagramPoster.post(smpost);
    		}
    	} else {
    		website = website + "0";
    	}
    	if(post.isPostToFacebook()) {
    		//FacebookPoster facebookPoster = new FacebookPoster();
    		website = website + "1";
    		if(facebookPoster != null) {
    			facebookPostResult = facebookPoster.post(smpost);
    		}
    	} else {
    		website = website + "0";
    	}
    	//if(instagramPostResult && facebookPostResult && twitterPostResult) {
    		smpost.setWebsite(website);
    		postDao.addPost(smpost, fileDao);
    		
    	//}
    	
    	return "createPost";
    }
    
    String url = "";
    
    @GetMapping(value = "/createPost")
	public String showPostUpload(@RequestParam(name = "url", required=false, defaultValue="") String url, SMPost post, Model model) throws IOException {
		model.addAttribute("post", new SMPost());
		model.addAttribute("url", url);
		this.url = url;
		return "createPost";
	}
    
    @PostMapping(value = "/twitterLogin")
	public String getTwitterPinPage(@Valid @ModelAttribute("pin") TwitterPin twitterPin, BindingResult result, Model model) {
		this.twitterPin = twitterPin;
    	twitterPoster.getAccessTokenFromPIN(twitterPin.getPin());
		return "createPost";
	}
    
    @GetMapping(value ="/restoreAll")
    public String restoreAllPosts(Model model) {
    	ArrayList<StoredSMPost> posts = postDao.getAll(owner);
    	for(int i = 0; i < posts.size(); i++) {
    		StoredSMPost post = posts.get(i);
    		if(post.isPostToFacebook() && facebookPoster != null) {
    			facebookPoster.restore(post);
    		} 
    		if(post.isPostToInstagram() && instagramPoster != null) {
    			instagramPoster.restore(post);
    		}
    		if(post.isPostToTwitter() && twitterPoster != null) {
    			twitterPoster.restore(post);
    		}
    	}
    	return "confirmation";
    }
     
    @GetMapping(value = "/twitterLogin")
	public String showTwitterLoginPage(TwitterPin twitterPin, Model model) throws IOException {
    	twitterPoster = new TwitterPoster(fileDao);
    	twitterPin = new TwitterPin();
		twitterPin.setURL(twitterPoster.getAuthenticationURL()); 
		model.addAttribute("twitterPin", twitterPin);
		return "twitterPin";
	}
    
    @GetMapping(value = "/IGtoken")
	public String getIGAccessToken(@RequestParam(name = "access_token", required=false, defaultValue="0") String token, Model model) {
    	if(token.equals("0")) {
    		return "home";
    	} 
    	instagramPoster = new InstagramPoster(fileDao);
    	instagramPoster.setAccessToken(token);
    	System.out.println(token);
    	return "confirmation";
    }
    
    @GetMapping(value = "/FBtoken")
	public String getFBAccessToken(@RequestParam(name = "access_token", required=false, defaultValue="0") String token, Model model) {
    	if(token.equals("0")) {
    		return "home";
    	} 
    	facebookPoster = new FacebookPoster(fileDao);
    	facebookPoster.setAccessToken(token);
    	System.out.println(token);
    	return "confirmation";
    }
    
    @GetMapping("/selectFile")
    public String selectUploadedFiles(Model model) throws IOException {
    	ArrayList<FilenameURLPair> listOfFiles = fileDao.getFileNamesAndURLs(owner);
    	model.addAttribute("file", listOfFiles);
        return "selectFile";
       
    }
    
    
    
    
}