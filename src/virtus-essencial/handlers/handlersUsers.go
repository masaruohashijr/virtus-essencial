package handlers

import (
	"fmt"
	"golang.org/x/crypto/bcrypt"
	"html/template"
	"log"
	"math/rand"
	"net/http"
	"net/smtp"
	"strconv"
	"strings"
	"time"
	mdl "virtus-essencial/models"
	route "virtus-essencial/routes"
	sec "virtus-essencial/security"
)

func CreateUserHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Create User")
	log.Println(r.Method)
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		currentUser := GetUserInCookie(w, r)
		name := r.FormValue("Name")
		log.Println(name)
		username := r.FormValue("Username")
		log.Println(username)
		password := r.FormValue("Password")
		log.Println(password)
		email := r.FormValue("Email")
		log.Println(email)
		mobile := r.FormValue("Mobile")
		log.Println(mobile)
		role := r.FormValue("Role")
		log.Println(role)
		hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
		log.Println(hash)
		statusUsuarioId := GetStartStatus("usuario")
		sqlStatement := "INSERT INTO virtus.users(name, username, password, email, mobile, id_role, id_author, criado_em, id_status) " +
			" OUTPUT INSERTED.id_user VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)"
		id := 0
		err = Db.QueryRow(sqlStatement, name, username, hash, email, mobile, role, currentUser.Id, statusUsuarioId).Scan(&id)
		if err != nil {
			log.Println(err.Error())
			errMsg := "Erro ao criar Usuário."
			if role == "" {
				errMsg = errMsg + " Faltou informar o Perfil do Usuário."
			}
			http.Redirect(w, r, route.UsersRoute+"?errMsg="+errMsg, 301)
		} else {
			log.Println("INSERT: Id: " + strconv.Itoa(id) +
				" | Name: " + name + " | Username: " + username +
				" | Password: " + password + " | Email: " + email +
				" | Mobile: " + mobile + " | Role: " + role)
			http.Redirect(w, r, route.UsersRoute+"?msg=Usuário criado com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateUserHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update User")
	log.Println(r.Method)
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		id := r.FormValue("Id")
		name := r.FormValue("Name")
		username := r.FormValue("Username")
		email := r.FormValue("Email")
		mobile := r.FormValue("Mobile")
		role := r.FormValue("RoleForUpdate")
		log.Println("Role: " + role)
		sqlStatement := " UPDATE virtus.Users SET name=?, " +
			" username=?, email=?, mobile=?, id_role=? " +
			" WHERE id_user=? "
		log.Println(sqlStatement)
		updtForm, err := Db.Prepare(sqlStatement)
		if err != nil {
			log.Println(err.Error())
		}
		updtForm.Exec(name, username, email, mobile, role, id)
		log.Println("UPDATE: Id: " +
			id + " | Name: " +
			name + " | Username: " +
			username + " | E-mail: " +
			email + " | Mobile: " +
			mobile + " | Role: " +
			role)
		http.Redirect(w, r, route.UsersRoute+"?msg=Usuário atualizado com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DeleteUserHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Delete User")
	log.Println(r.Method)
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		errMsg := "Usuário vinculado a registro não pode ser removido."
		id := r.FormValue("Id")
		sqlStatement := "DELETE FROM virtus.users WHERE id_user=?"
		deleteForm, _ := Db.Prepare(sqlStatement)
		_, err := deleteForm.Exec(id)
		if err != nil && strings.Contains(err.Error(), "violates foreign key") {
			http.Redirect(w, r, route.UsersRoute+"?errMsg="+errMsg, 301)
		} else {
			http.Redirect(w, r, route.UsersRoute+"?msg=Usuário removido com sucesso.", 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func SendPasswordHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Send Password")
	log.Println(r.Method)
	// Sender data.
	from := "virtusimpavidas@gmail.com"
	emailPassword := "v1rtudesemmedo"
	subject := "Sistema Virtus"
	emailTo := r.FormValue("Email")
	// Receiver email address.
	to := []string{
		emailTo,
	}

	// smtp server configuration.
	smtpHost := "smtp.gmail.com"
	smtpPort := "587"
	rand.Seed(time.Now().Unix())
	minSpecialChar := 1
	minNum := 1
	minUpperCase := 1
	passwordLength := 8
	password := generatePassword(passwordLength, minSpecialChar, minNum, minUpperCase)
	log.Println(password)
	user := loadUserByEmail(emailTo)
	if user.Id == 0 {
		errMsg := "?errMsg=Email não encontrado!"
		log.Println(errMsg)
		http.Redirect(w, r, "/logout"+errMsg, 301)
		return
	}
	hash, _ := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	atualizarSenha(hash, strconv.FormatInt(user.Id, 10))
	// Message.
	message := []byte("To: " + emailTo + "\r\n" +
		"Subject: " + subject + "\r\n" +
		"\r\n" +
		"Username: " + user.Username + "\r\n" +
		"Nova senha: " + password + "\r\n")
	log.Println(message)

	// Authentication.
	auth := smtp.PlainAuth("", from, emailPassword, smtpHost)

	// Sending email.
	err := smtp.SendMail(smtpHost+":"+smtpPort, auth, from, to, message)
	if err != nil {
		fmt.Println(err)
		return
	}
	msg := "?msg=Email enviado com Sucesso!"
	log.Println(msg)
	http.Redirect(w, r, "/logout"+msg, 301)
}

func loadUserByEmail(emailTo string) mdl.User {
	rows, _ := Db.Query("SELECT id_user, username FROM virtus.users WHERE email = ?", emailTo)
	var user mdl.User
	if rows.Next() {
		rows.Scan(&user.Id, &user.Username)
	}
	return user
}

func RecoverPasswordHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Recover Password")
	log.Println(r.Method)
	http.ServeFile(w, r, "tiles/users/Recover-Passwd.html")
}

func ChangePasswordHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Change Password")
	log.Println(r.Method)
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		//		currentUser := GetUserInCookie(w, r)
		id := r.FormValue("Id")
		log.Println(id)
		username := r.FormValue("Username")
		log.Println(username)
		password := r.FormValue("Password")
		log.Println(password)
		hash, _ := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
		atualizarSenha(hash, id)
		http.Redirect(w, r, route.UsersRoute+"?msg=Nova senha atualizada com sucesso.", 301)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func atualizarSenha(hash []byte, id string) {
	sqlStatement := "UPDATE virtus.Users SET password = '" + string(hash[:]) +
		"' WHERE id_user = " + id
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func RegisterNewUserHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Register New User")
	log.Println(r.Method)
	name := r.FormValue("Name")
	username := r.FormValue("Username")
	password := r.FormValue("Password")
	//	log.Println(password)
	email := r.FormValue("Email")
	mobile := r.FormValue("Mobile")
	hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	log.Println(hash)
	statusUsuarioId := GetStartStatus("usuario")
	sqlStatement := "INSERT INTO virtus.users(name, " +
		" username, " +
		" password, " +
		" email, " +
		" mobile, " +
		" id_role, " +
		" criado_em, " +
		" id_status) " +
		" OUTPUT INSERTED.id_user " +
		" VALUES ( '" + name + "', " +
		" '" + username + "', " +
		" '" + string(hash[:]) + "', " +
		" '" + email + "', " +
		" '" + mobile + "', " +
		" 5, " +
		" GETDATE(), " +
		" " + strconv.Itoa(statusUsuarioId) + ") "
	log.Println(sqlStatement)
	id := 0
	err = Db.QueryRow(sqlStatement).Scan(&id)
	if err != nil {
		log.Println(err.Error())
		errMsg := "Erro ao criar Usuário."
		http.Redirect(w, r, route.UsersRoute+"?errMsg="+errMsg, 301)
	} else {
		log.Println("INSERT: Id: " + strconv.Itoa(id) +
			" | Name: " + name + " | Username: " + username +
			" | Password: " + password + " | Email: " + email +
			" | Mobile: " + mobile)
		msg := "?msg=Seu registro foi efetuado com sucesso.\n Após nossa análise, você receberá um e-mail de confirmação."
		log.Println(msg)
		http.Redirect(w, r, "/logout"+msg, 301)
	}
}

func SignUpUserHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Sign-Up Users")
	log.Println(r.Method)
	http.ServeFile(w, r, "tiles/users/Sign-Up-User.html")
}

func PasswordHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Mudar Senha")
	var page mdl.PageUsers
	page.AppName = mdl.AppName
	page.Title = "Mudar Senha" + mdl.Ambiente
	page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
	var tmpl = template.Must(template.ParseGlob("tiles/users/*"))
	tmpl.ParseGlob("tiles/*")
	tmpl.ExecuteTemplate(w, "Main-MyUser", page)
}

func ListUsersHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Users")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listUsers") {
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		sql := "SELECT " +
			" a.id_user, a.name, a.username, a.password, " +
			" a.email, a.mobile, " +
			" COALESCE(a.id_role, 0), COALESCE(b.name,'') as role_name, " +
			" coalesce(a.id_author,0) as id_author, " +
			" coalesce(e.name,'') as author_name, " +
			" format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'), " +
			" coalesce(d.name,'') as cstatus, " +
			" coalesce(a.id_status,0) , " +
			" coalesce(a.id_versao_origem,0) " +
			" FROM virtus.users a " +
			" LEFT JOIN virtus.roles b ON a.id_role = b.id_role " +
			" LEFT JOIN virtus.status d ON a.id_status = d.id_status " +
			" LEFT JOIN virtus.users e ON a.id_author = e.id_user " +
			" ORDER BY a.name ASC "
		log.Println("SQL: " + sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var users []mdl.User
		var user mdl.User
		var i = 1
		for rows.Next() {
			rows.Scan(&user.Id,
				&user.Name,
				&user.Username,
				&user.Password,
				&user.Email,
				&user.Mobile,
				&user.Role,
				&user.RoleName,
				&user.AuthorId,
				&user.AuthorName,
				&user.C_CriadoEm,
				&user.CStatus,
				&user.StatusId,
				&user.IdVersaoOrigem)
			user.Order = i
			i++
			log.Println(user)
			users = append(users, user)
		}
		sql = "SELECT id_role, name FROM virtus.roles ORDER BY name asc"
		log.Println("SQL Roles: " + sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var roles []mdl.Role
		var role mdl.Role
		i = 1
		for rows.Next() {
			rows.Scan(&role.Id,
				&role.Name)
			role.Order = i
			i++
			roles = append(roles, role)
		}
		var page mdl.PageUsers
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Users = users
		page.Roles = roles
		page.AppName = mdl.AppName
		page.Title = "Usuários" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/users/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Users", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

var (
	lowerCharSet   = "abcdedfghijklmnopqrst"
	upperCharSet   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	specialCharSet = "!@#$%&*"
	numberSet      = "0123456789"
	allCharSet     = lowerCharSet + upperCharSet + specialCharSet + numberSet
)

func generatePassword(passwordLength, minSpecialChar, minNum, minUpperCase int) string {
	var password strings.Builder

	//Set special character
	for i := 0; i < minSpecialChar; i++ {
		random := rand.Intn(len(specialCharSet))
		password.WriteString(string(specialCharSet[random]))
	}

	//Set numeric
	for i := 0; i < minNum; i++ {
		random := rand.Intn(len(numberSet))
		password.WriteString(string(numberSet[random]))
	}

	//Set uppercase
	for i := 0; i < minUpperCase; i++ {
		random := rand.Intn(len(upperCharSet))
		password.WriteString(string(upperCharSet[random]))
	}

	remainingLength := passwordLength - minSpecialChar - minNum - minUpperCase
	for i := 0; i < remainingLength; i++ {
		random := rand.Intn(len(allCharSet))
		password.WriteString(string(allCharSet[random]))
	}
	inRune := []rune(password.String())
	rand.Shuffle(len(inRune), func(i, j int) {
		inRune[i], inRune[j] = inRune[j], inRune[i]
	})
	return string(inRune)
}
